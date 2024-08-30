package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.utils.FontUtils;

public class ResultsScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private FrameBuffer frameBuffer;

    private BitmapFont fontHeading, fontText;
    private GlyphLayout layout;

    private Constants.GameResult result;
    private String headingText;

    private GameGrid resultGrid;

    public ResultsScreen(Game game, GameGrid grid, Constants.GameResult gameResult) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Constants.GAME_WIDTH, Constants.GAME_HEIGHT, true);

        result = gameResult;
        headingText = result.getResultHeading();

        layout = new GlyphLayout();
        fontHeading = FontUtils.monofett(120);
        fontHeading.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30);
        fontText.setColor(Color.BLACK);


        resultGrid = grid;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(new Color(0xFFCCBFFF));

        viewport.apply();

        frameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        resultGrid.drawGrid(batch);
        resultGrid.drawBoxes(batch);

        batch.end();
        frameBuffer.end();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawCenteredText(headingText, fontHeading, Constants.GAME_WIDTH / 2, Constants.GAME_HEIGHT);
        drawCenteredText("Press ENTER to return to the Main Menu", fontText, Constants.GAME_WIDTH / 2, 100);

        TextureRegion frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        frameBufferRegion.flip(false, true);

        int w = frameBufferRegion.getRegionWidth() / 2;
        int h = frameBufferRegion.getRegionHeight() / 2;

        batch.draw(frameBufferRegion, Constants.GAME_WIDTH / 2 - w / 2, Constants.GAME_HEIGHT / 2 - h / 2, w, h);

        batch.end();

        handleInput();
    }

    private void drawCenteredText(String text, BitmapFont font, int posX, int posY) {
        layout.setText(font, text);
        int textWidth = (int) layout.width;
        int textHeight = (int) layout.height;

        int x = posX - textWidth / 2;
        int y = posY - textHeight / 2;

        font.draw(batch, text, x, y);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new FirstScreen(game));
            dispose();
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
        fontHeading.dispose();
        fontText.dispose();
        resultGrid.dispose();
        frameBuffer.dispose();
        batch.dispose();
    }
}
