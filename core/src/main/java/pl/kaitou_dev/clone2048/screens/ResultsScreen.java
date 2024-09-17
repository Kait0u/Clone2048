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
import pl.kaitou_dev.clone2048.utils.timed_actions.Blinker;

/**
 * The results screen of the application. Its contents are dependent on the result of the game.
 */
public class ResultsScreen implements Screen {
    /**
     * The {@link Game} instance, required for switching between the screens.
     */
    private final Game game;
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
     * A {@link Blinker} that will cause prompts to blink.
     */
    private final Blinker blinker;

    /**
     * A font to be used to render the heading.
     */
    private final BitmapFont fontHeading;
    /**
     * A font to be used to render the text and prompts.
     */
    private final BitmapFont fontText;

    /**
     * The text to be displayed as the heading.
     */
    private final String headingText;

    /**
     * The sprite that represents the final situation on the {@link GameGrid}.
     */
    private final Sprite gridSprite;

    /**
     * The default constructor, which prepares the screen by accepting a reference to the {@link Game},
     * as well as prepares the results view by accepting the {@link GameGrid} of the last game,
     * and the result of the game.
     * @param game An instance of the current {@link Game} object.
     * @param grid The {@code GameGrid} from the previous screen.
     * @param gameResult The result coming from the previous screen.
     */
    public ResultsScreen(Game game, GameGrid grid, Constants.GameResult gameResult) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        headingText = gameResult.getResultHeading();

        fontHeading = FontUtils.monofett(120 * Constants.UNIT_FONT_SIZE);
        fontHeading.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30 * Constants.UNIT_FONT_SIZE);
        fontText.setColor(Color.BLACK);

        gridSprite = getGridSprite(grid);
        grid.dispose();

        blinker = new Blinker(Constants.DEFAULT_BLINK, Constants.DEFAULT_BLINK, true);
    }

    @Override
    public void show() {
        blinker.start();
    }

    /**
     * Updates this screen's children using the delta-time.
     * @param delta The delta-time.
     */
    public void update(float delta) {
        blinker.actWithDelta(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(new Color(0xFFCCBFFF));

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        GraphicsUtils.drawCenteredTextLine(
            batch, headingText, fontHeading, Constants.GAME_WIDTH / 2, (int) (Constants.GAME_HEIGHT - fontHeading.getCapHeight())
        );

        if (blinker.isOn())
            GraphicsUtils.drawCenteredTextLine(
                batch, "Press ENTER to return to the Main Menu", fontText, Constants.GAME_WIDTH / 2, 100
        );

        int w = gridSprite.getRegionWidth() / 2;
        int h = gridSprite.getRegionHeight() / 2;

        batch.draw(gridSprite, Constants.GAME_WIDTH / 2 - w / 2, Constants.GAME_HEIGHT / 2 - h / 2, w, h);
        batch.end();

        handleInput();
    }

    /**
     * Handles the input for this screen.
     */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new FirstScreen(game));
            dispose();
        }
    }

    /**
     * Turns the {@link GameGrid} into a sprite, for resource efficiency.
     * @param grid The {@code GameGrid} from the previous screen.
     * @return A {@link Sprite} representing the final situation on the grid.
     */
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        fontHeading.dispose();
        fontText.dispose();
        gridSprite.getTexture().dispose();
        batch.dispose();
    }
}
