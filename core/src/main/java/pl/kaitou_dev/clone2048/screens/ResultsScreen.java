package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.GraphicsUtils;

public class ResultsScreen implements Screen {
    private final Game game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;

    private final BitmapFont fontHeading;
    private final BitmapFont fontText;

    private final String headingText;

    private final Sprite gridSprite;

    public ResultsScreen(Game game, GameGrid grid, Constants.GameResult gameResult) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        headingText = gameResult.getResultHeading();

        fontHeading = FontUtils.monofett(120);
        fontHeading.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30);
        fontText.setColor(Color.BLACK);

        gridSprite = getGridSprite(grid);
        grid.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(new Color(0xFFCCBFFF));

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        GraphicsUtils.drawCenteredTextLine(
            batch, headingText, fontHeading, Constants.GAME_WIDTH / 2, Constants.GAME_HEIGHT
        );
        GraphicsUtils.drawCenteredTextLine(
            batch, "Press ENTER to return to the Main Menu", fontText, Constants.GAME_WIDTH / 2, 100
        );

        int w = gridSprite.getRegionWidth() / 2;
        int h = gridSprite.getRegionHeight() / 2;

        batch.draw(gridSprite, Constants.GAME_WIDTH / 2 - w / 2, Constants.GAME_HEIGHT / 2 - h / 2, w, h);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new FirstScreen(game));
            dispose();
        }
    }

    private Sprite getGridSprite(GameGrid grid) {
        FrameBuffer frameBuffer = new FrameBuffer(
            Pixmap.Format.RGBA8888, Constants.GAME_WIDTH, Constants.GAME_HEIGHT, true
        );

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
        frameBuffer.dispose();

        Texture tex = new Texture(pixmap);
        pixmap.dispose();

        Sprite sprite = new Sprite(tex);
        sprite.flip(false, true);

        return sprite;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        fontHeading.dispose();
        fontText.dispose();
        gridSprite.getTexture().dispose();
        batch.dispose();
    }
}
