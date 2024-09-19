package pl.kaitou_dev.clone2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import pl.kaitou_dev.clone2048.screens.FirstScreen;
import pl.kaitou_dev.clone2048.utils.AudioUtils;
import pl.kaitou_dev.clone2048.utils.platform_specific.ErrorDisplayer;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 * It is implemented akin to a singleton because it behaves like a singleton,
 * even if LibGDX did not make it an explicit singleton.
 */
public class Clone2048 extends Game {
    /**
     * The instance of the game.
     */
    private static Clone2048 instance;

    /**
     * The platform dependent implementation of {@link ErrorDisplayer}.
     */
    private static ErrorDisplayer errorDisplayer = new ErrorDisplayer() {
        @Override
        public void displayError(String title, String message) {
            RuntimeException exception = new RuntimeException(title + ": " + message);
            exception.printStackTrace();
        }
    };

    /**
     * A private constructor for the creation of an instance.
     */
    private Clone2048() {
        super();
    }

    @Override
    public void create() {
        try {
            setScreen(new FirstScreen());
        } catch (Exception e) {
            handleError(e);
        }
    }

    @Override
    public void render() {
        try {
            super.render();
        } catch (Exception e) {
            handleError(e);
        }
    }

    @Override
    public void pause() {
        try {
            super.pause();
        } catch (Exception e) {
            handleError(e);
        }
    }

    @Override
    public void resume() {
        try {
            super.resume();
        } catch (Exception e) {
            handleError(e);
        }
    }

    @Override
    public void dispose() {
        AudioUtils.dispose();
    }

    /**
     * Gets the instance of the {@code Clone2048} object.
     * @return The instance of the {@code Clone2048} object.
     */
    public static Clone2048 getInstance() {
        if (instance == null) {
            instance = new Clone2048();
        }

        return instance;
    }

    /**
     * Sets the {@link ErrorDisplayer} implementation - to be used by platform-specific wrappers.
     * @param errorDisplayer The {@code ErrorDisplayer} implementation to override the current one with.
     */
    public void setErrorDisplayer(ErrorDisplayer errorDisplayer) {
        Clone2048.errorDisplayer = errorDisplayer;
    }

    /**
     * Handles an exception by displaying it on the screen.
     * @param e The exception to display.
     */
    public void handleError(Exception e) {
        errorDisplayer.displayError(e);
        dispose();
        Gdx.app.exit();
    }
}
