package pl.kaitou_dev.clone2048.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import pl.kaitou_dev.clone2048.Clone2048;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.platform_specific.Confirmer;
import pl.kaitou_dev.clone2048.utils.platform_specific.ErrorDisplayer;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    /**
     * The default width of the desktop window, in pixels.
     */
    private static final int GAME_DESKTOP_WIDTH = Constants.GAME_WIDTH;
    /**
     * The default height of the desktop window, in pixels.
     */
    private static final int GAME_DESKTOP_HEIGHT = Constants.GAME_HEIGHT;

    /**
     * The Lwjgl3 implementation of {@link ErrorDisplayer}.
     */
    private static final Lwjgl3ErrorDisplayer ERROR_DISPLAYER = new Lwjgl3ErrorDisplayer();

    /**
     * The Lwjgl3 implementation of {@link Confirmer}.
     */
    private static final Confirmer CONFIRMER = new Lwjgl3Confirmer();

    /**
     * The entry point to the desktop wrapper for the game.
     * @param args Launch arguments.
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        Lwjgl3Application app = createApplication();
    }

    /**
     * Creates the application.
     * @return An {@link Lwjgl3Application} instance with the game.
     */
    private static Lwjgl3Application createApplication() {
        Clone2048 game = Clone2048.getInstance();
        game.setErrorDisplayer(ERROR_DISPLAYER);
        game.setConfirmer(CONFIRMER);
        return new Lwjgl3Application(game, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Clone2048");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(GAME_DESKTOP_WIDTH, GAME_DESKTOP_HEIGHT);
        configuration.setWindowSizeLimits(
            Constants.GAME_WIDTH, Constants.GAME_HEIGHT, Constants.MAX_WIDTH, Constants.MAX_HEIGHT
        );
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 4, 16);

        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        configuration.setWindowIcon("icon128.png", "icon64.png", "icon32.png", "icon16.png");

        configuration.setWindowListener(
            new Lwjgl3WindowAdapter() {
                @Override
                public boolean closeRequested() {
                    return CONFIRMER.askConfirm("Are you sure you want to exit?");
                }
            }
        );

        return configuration;
    }
}
