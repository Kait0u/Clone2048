package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.Directions;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;

public class GameScreen implements Screen {
    private Game game;

    private OrthographicCamera camera;
    private FitViewport viewport;

    private SpriteBatch spriteBatch;


    // Game objects

    private GameGrid gameGrid;


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
        gameGrid.setCoords(Constants.GAME_WIDTH / 2 - GameGrid.SIZE / 2, Constants.GAME_HEIGHT / 2 - GameGrid.SIZE / 2);
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

    private void update(float delta) {
        gameGrid.update(delta);

        switch (gameGrid.getState()) {
            case GAME_OVER -> {
                game.setScreen(new ResultsScreen(game, Constants.GameResult.GAME_OVER));
                dispose();
            }
            case VICTORY -> {
                game.setScreen(new ResultsScreen(game, Constants.GameResult.VICTORY));
                dispose();
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameGrid.addNewBox(1024);
        }

        gameGrid.handleInput();
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
        gameGrid.dispose();
        spriteBatch.dispose();
    }
}
