package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.GameGrid;
import pl.kaitou_dev.clone2048.utils.PixmapUtils;

public class GameScreen implements Screen {
    private Game game;

    private OrthographicCamera camera;
    private FitViewport viewport;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private GameGrid gameGrid;

    ///////////////////////////////////////
    // TO DELETE
    ///////////////////////////////////////


    Texture tx;
    int w = 200, h = 200;

    ///////////////////////////////////////
    // END TO DELETE
    ///////////////////////////////////////

    public GameScreen(Game game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);

        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        gameGrid = new GameGrid();
        gameGrid.setCoords(Constants.GAME_WIDTH / 2 - GameGrid.SIZE / 2, Constants.GAME_HEIGHT / 2 - GameGrid.SIZE / 2);


        Pixmap pm = PixmapUtils.getRoundRectPixmap(w, h, w / 5, Color.VIOLET);
        tx = new Texture(pm);
        tx.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        tx.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        pm.dispose();
    }

    @Override
    public void show() {
        System.out.println(gameGrid);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(new Color(0xFFFFFFFF));

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
//        spriteBatch.draw(tx, Constants.GAME_WIDTH / 2 - w / 2, Constants.GAME_HEIGHT / 2 - h / 2);
        gameGrid.drawGrid(spriteBatch);
        gameGrid.drawBoxes(spriteBatch);
        spriteBatch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameGrid.addNewBox();
        }
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
        tx.dispose();
        spriteBatch.dispose();
    }
}
