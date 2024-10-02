package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Clone2048;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.utils.AudioUtils;

/**
 * Represents the actual game screen, and contains all the rendering logic.
 */
public class GameScreen implements Screen {
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
    private final SpriteBatch spriteBatch;


    // Game objects
    /**
     * The {@link GameGrid} that a game of 2048 requires.
     */
    private GameGrid gameGrid;

    /**
     * The default constructor which and sets up the basic components to display the screen.
     */
    public GameScreen() {
        this.game = Clone2048.getInstance();

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

        handleInput();

        update(delta);
    }

    /**
     * Updates the children objects of this screen using the delta-time.
     * After that, checks if the game can end, and with what result, and if it can - changes the screen appropriately.
     * @param delta The delta-time.
     */
    private void update(float delta) {
        gameGrid.update(delta);


        if (gameGrid.isGameOver())
            goToResults(Constants.GameResult.GAME_OVER);
        else if (gameGrid.isVictory())
            goToResults(Constants.GameResult.VICTORY);
    }

    /**
     * Goes to the results screen with a specified result.
     * @param result The result of a game, specified by {@link Constants.GameResult}
     */
    private void goToResults(Constants.GameResult result) {
        game.setScreen(new ResultsScreen(gameGrid, result));
        dispose();
    }

    /**
     * Handles the input from the user.
     */
    private void handleInput() {
        gameGrid.handleInput();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handleQuit();
        }
    }

    private void handleQuit() {
        if (game.askConfirm("Are you sure you want to give up?"))
            goToResults(Constants.GameResult.GAME_OVER);
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
