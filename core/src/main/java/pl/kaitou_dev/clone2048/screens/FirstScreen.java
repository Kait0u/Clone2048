package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.Directions;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.GraphicsUtils;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.timed_actions.Blinker;

import java.util.Arrays;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private final static float ANIMATION_INTERVAL_SECONDS = 1.0f;

    private final Game game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;
    private final FrameBuffer frameBuffer;

    private final BitmapFont fontLogo;
    private final BitmapFont fontText;
    private final BitmapFont fontCredits;

    private GameGrid grid;
    private float timeSinceLastMove = 0;
    private final Blinker blinker;

    public FirstScreen(Game game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        frameBuffer = new FrameBuffer(
            Pixmap.Format.RGBA8888, Constants.GAME_WIDTH, Constants.GAME_HEIGHT, true
        );

        fontLogo = FontUtils.monofett(120);
        fontLogo.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30);
        fontText.setColor(Color.BLACK);
        fontCredits = FontUtils.losevka(15);
        fontCredits.setColor(Color.BLACK);


        grid = new GameGrid(false);
        grid.setCoords(Constants.GAME_WIDTH / 2 - GameGrid.SIZE / 2, Constants.GAME_HEIGHT / 2 - GameGrid.SIZE / 2);

        blinker = new Blinker(Constants.DEFAULT_BLINK, Constants.DEFAULT_BLINK, true);
    }

    @Override
    public void show() {
        // Prepare your screen here.
        blinker.start();
    }

    public void update(float delta) {
        blinker.actWithDelta(delta);

        grid.update(delta);

        if (grid.isVictory() || grid.isGameOver()) {
            grid.dispose();
            grid = new GameGrid(false);
            grid.setCoords(Constants.GAME_WIDTH / 2 - GameGrid.SIZE / 2, Constants.GAME_HEIGHT / 2 - GameGrid.SIZE / 2);
        }

        animateGrid();
        timeSinceLastMove += delta;
    }

    private void animateGrid() {
        if (timeSinceLastMove < ANIMATION_INTERVAL_SECONDS) return;

        if (!grid.isBusy()) {
            Directions[] directions = Arrays.stream(Directions.values())
                .filter(grid::isMovementPossible)
                .toArray(Directions[]::new);

            if (directions.length > 0) {
                Directions direction = directions[MathNumUtils.randInt(directions.length)];
                grid.move(direction);
                grid.addNewBox();
                grid.updateLegalMoves();

                timeSinceLastMove = 0;
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Draw your screen here. "delta" is the time since last render in seconds.
        ScreenUtils.clear(new Color(0xFFCCBFFF));

        viewport.apply();
        frameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        grid.drawGrid(batch);
        grid.drawBoxes(batch);

        batch.end();
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        frameBuffer.end();

        Texture gridTexture = new Texture(pixmap);
        pixmap.dispose();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int w = gridTexture.getWidth() / 2;
        int h = gridTexture.getHeight() / 2;

        batch.draw(gridTexture, Constants.GAME_WIDTH / 2 - w / 2, Constants.GAME_HEIGHT / 2 - h / 2, w, h);

        GraphicsUtils.drawCenteredTextLine(
            batch, "2048", fontLogo, Constants.GAME_WIDTH / 2, (int) (Constants.GAME_HEIGHT - fontLogo.getCapHeight())
        );
        if (blinker.isOn())
            GraphicsUtils.drawCenteredTextLine(
                batch, "Press ENTER to begin", fontText, Constants.GAME_WIDTH / 2, 50 + (int) fontText.getCapHeight()
            );

        GraphicsUtils.drawCenteredTextLine(
            batch, "©2024, Kait0u", fontCredits, Constants.GAME_WIDTH / 2, (int) fontCredits.getCapHeight()
        );

        batch.end();

        gridTexture.dispose();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        fontText.dispose();
        fontLogo.dispose();
        fontCredits.dispose();
        grid.dispose();
        frameBuffer.dispose();
        batch.dispose();
    }
}
