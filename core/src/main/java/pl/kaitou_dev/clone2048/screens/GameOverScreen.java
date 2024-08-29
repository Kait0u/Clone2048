package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.FontUtils;

public class GameOverScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private BitmapFont fontHeading, fontText;
    private GlyphLayout layout;

    public GameOverScreen(Game game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        layout = new GlyphLayout();
        fontHeading = FontUtils.monofett(120);
        fontHeading.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30);
        fontText.setColor(Color.BLACK);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawCenteredText("GAME OVER", fontHeading, Constants.GAME_WIDTH / 2, 100);

        batch.end();
    }

    private void drawCenteredText(String text, BitmapFont font, int posX, int posY) {
        layout.setText(font, text);
        int textWidth = (int) layout.width;
        int textHeight = (int) layout.height;

        int x = posX - textWidth / 2;
        int y = posY - textHeight / 2;

        font.draw(batch, text, x, y);
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
