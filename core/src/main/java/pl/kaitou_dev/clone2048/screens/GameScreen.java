package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.utils.AudioUtils;

/**
 * Represents the actual game screen, and contains all the rendering logic.
 */
public class GameScreen implements Screen {
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
    private final SpriteBatch spriteBatch;


    // Game objects
    /**
     * The {@link GameGrid} that a game of 2048 requires.
     */
    private GameGrid gameGrid;

    /**
     * The default constructor, which takes a reference to the {@link Game} object
     * and sets up the basic components to display the screen.
     * @param game An instance of the current {@link Game} object.
     */
    public GameScreen(Game game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);

        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        spriteBatch = new SpriteBatch();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void show() {
        gameGrid = new GameGrid();
        gameGrid.setCoords(
            Constants.GAME_WIDTH / 2 - GameGrid.SIZE / 2,
            Constants.GAME_HEIGHT / 2 - GameGrid.SIZE / 2
        );

        AudioUtils.Sounds.GAME_START.play();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        gameGrid.drawGrid(spriteBatch);
        gameGrid.drawBoxes(spriteBatch);
        spriteBatch.end();

        update(delta);

        handleInput();
    }

    /**
     * Updates the children objects of this screen using the delta-time.
     * After that, checks if the game can end, and with what result, and if it can - changes the screen appropriately.
     * @param delta The delta-time.
     */
    private void update(float delta) {
        gameGrid.update(delta);

        if (gameGrid.isGameOver()) {
            game.setScreen(new ResultsScreen(game, gameGrid, Constants.GameResult.GAME_OVER));
            dispose();

        } else if (gameGrid.isVictory()) {
            game.setScreen(new ResultsScreen(game, gameGrid, Constants.GameResult.VICTORY));
            dispose();
        }
    }

    /**
     * Handles the input from the user.
     */
    private void handleInput() {
        gameGrid.handleInput();
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
        spriteBatch.dispose();
    }
}
