package pl.kaitou_dev.clone2048.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.FontUtils;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private BitmapFont fontLogo, fontText;
    private GlyphLayout layout;

    public FirstScreen(Game game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);

        batch = new SpriteBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        layout = new GlyphLayout();
        fontLogo = FontUtils.monofett(120);
        fontLogo.setColor(Color.BLACK);
        fontText = FontUtils.losevka(30);
        fontText.setColor(Color.BLACK);
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        ScreenUtils.clear(new Color(0xFFCCBFFF));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawCenteredText("2048", fontLogo, Constants.GAME_WIDTH / 2, Constants.GAME_HEIGHT);

        drawCenteredText("Press ENTER to begin", fontText, Constants.GAME_WIDTH / 2, 200);
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
        batch.dispose();
    }
}
