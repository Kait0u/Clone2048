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
import pl.kaitou_dev.clone2048.Clone2048;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.Directions;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.GraphicsUtils;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.timed_actions.Blinker;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen {
    /**
     * The animation interval, measured in seconds.
     * It measures how often this screen's animation should happen.
     */
    private final static float ANIMATION_INTERVAL_SECONDS = 1.0f;

    /**
     * The {@link Game} instance (specialized as {@link Clone2048}), required for switching between the screens.
     */
    private final Clone2048 game;

    /**
     * The {@link Camera} for graphics.
     */
    private final OrthographicCamera camera;
    /**
     * The {@link com.badlogic.gdx.utils.viewport.Viewport} to make the window scale well while on this screen.
     */
    private final FitViewport viewport;
    /**
     * The {@link SpriteBatch} to render everything onto.
     */
    private final SpriteBatch batch;
    /**
     * A {@link FrameBuffer} to act as a virtual screen on this screen, to display the animation.
     */
    private final FrameBuffer frameBuffer;

    /**
     * A font to be used to render the logo.
     */
    private final BitmapFont fontLogo;
    /**
     * A font to be used to render prompts.
     */
    private final BitmapFont fontText;
    /**
     * A font to be used to render the credits.
     */
    private final BitmapFont fontCredits;

    // Animation-related
    /**
     * A {@link GameGrid} to animate.
     */
    private GameGrid grid;
    /**
     * The time elapsed since the last move of the animation, measured in seconds.
     */
    private float timeSinceLastMove = 0;
    /**
     * A {@link Blinker} that will cause prompts to blink.
     */
    private final Blinker blinker;

    /**
     * The default constructor which sets up the basic components to display the screen.
     */
    public FirstScreen() {
        this.game = Clone2048.getInstance();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        frameBuffer = new FrameBuffer(
            Pixmap.Format.RGBA8888, Constants.GAME_WIDTH, Constants.GAME_HEIGHT, true
        );

        fontLogo = FontUtils.monofett(120 * Constants.UNIT_FONT_SIZE);
        fontLogo.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30 * Constants.UNIT_FONT_SIZE);
        fontText.setColor(Color.BLACK);
        fontCredits = FontUtils.losevka(15 * Constants.UNIT_FONT_SIZE);
        fontCredits.setColor(Color.BLACK);

        createGrid();

        blinker = new Blinker(Constants.DEFAULT_BLINK, Constants.DEFAULT_BLINK, true);
    }

    /**
     * Creates a {@link GameGrid} for the animation purposes.
     */
    private void createGrid() {
        grid = new GameGrid(false);
        grid.setCoords(Constants.GAME_WIDTH / 2 - GameGrid.SIZE / 2, Constants.GAME_HEIGHT / 2 - GameGrid.SIZE / 2);
        grid.setSoundOn(false);
    }

    @Override
    public void show() {
        // Prepare your screen here.
        blinker.start();
    }

    /**
     * Updates the children objects of this screen using the delta-time.
     * Controls the flow of the animation, and restarts it if it is necessary.
     * @param delta The delta-time.
     */
    public void update(float delta) {
        blinker.actWithDelta(delta);

        grid.update(delta);

        if (grid.isVictory() || grid.isGameOver()) {
            grid.dispose();
            createGrid();
        }

        animateGrid();
        timeSinceLastMove += delta;
    }

    /**
     * Handles the animation of the grid, by trying to issue a random move to this screen's {@link GameGrid}
     * every {@link FirstScreen#ANIMATION_INTERVAL_SECONDS} seconds. The timer does not reset if the move was illegal,
     * so that it has a chance to make a correct guess next time this method is called.
     */
    private void animateGrid() {
        if (timeSinceLastMove < ANIMATION_INTERVAL_SECONDS) return;

        if (!grid.isBusy()) {
            Directions[] directions = Directions.values();
            grid.move(MathNumUtils.randChoice(directions));
            if (grid.isBusy()) timeSinceLastMove = 0;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Draw your screen here. "delta" is the time since last render in seconds.
        ScreenUtils.clear(new Color(0xFFCCBFFF));

        Pixmap pixmap = createGridPixmap();

        Texture gridTexture = new Texture(pixmap);
        pixmap.dispose();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int w = gridTexture.getWidth() * 2 / 3;
        int h = gridTexture.getHeight() * 2 / 3;

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

    /**
     * Creates a {@link Pixmap} out of this screen's {@link GameGrid} using a {@link FrameBuffer}.
     * @return A {@code Pixmap} of the current situation of the grid.
     */
    private Pixmap createGridPixmap() {
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
        return pixmap;
    }

    /**
     * Handles the input on this screen.
     */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen());
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            exitGame();
    }

    /**
     * Prompts the user if they want to quit the game, and quits the game if the user confirms that decision.
     * Does nothing else if declined.
     */
    public void exitGame() {
        if (game.askConfirm("Are you sure you want to exit?"))
            Gdx.app.exit();
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
