package pl.kaitou_dev.clone2048.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.kaitou_dev.clone2048.Clone2048;
import pl.kaitou_dev.clone2048.Constants;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static final int GAME_DESKTOP_WIDTH = 800, GAME_DESKTOP_HEIGHT = 600;


    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Clone2048(), getDefaultConfiguration());
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
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
