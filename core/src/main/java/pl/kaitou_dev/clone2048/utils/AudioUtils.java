package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Arrays;

/**
 * Contains categorized utilities for handling sounds and music.
 * WARNING: This class needs to dispose
 */
public class AudioUtils {
    /**
     * Gets a sound from the "audio/sounds" directory, given its filename.
     * @param filename The filename of the sound file.
     *                 WARNING: it is NOT a path, it is the name of the file in the assets/audio/sounds directory
     * @return A {@link Sound} made with the provided sound file.
     */
    private static Sound getSound(String filename) {
        return Gdx.audio.newSound(Gdx.files.internal("audio/sounds/" + filename));
    }

    /**
     * Gets a music track from the "audio/music" directory, given its filename.
     * @param filename The filename of the music file.
     *                 WARNING: it is NOT a path, it is the name of the file in the assets/audio/music directory
     * @return A {@link Music} made with the provided sound file.
     */
    private static Music getMusic(String filename) {
        return Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + filename));
    }

    /**
     * Assembles a bunch of sounds together for easier access. Every method of this class is
     * a getter of a specific {@link Sound} type.
     */
    public static class Sounds {
        /**
         * The game over sound.
         */
        public static final Sound GAME_OVER = getSound("gameOver.ogg");
        /**
         * The victory sound.
         */
        public static final Sound GAME_WIN = getSound("gameWin.ogg");
        /**
         * The game start sound.
         */
        public static final Sound GAME_START = getSound("gameStart.ogg");
        /**
         * The merge sound.
         */
        public static final Sound MERGE = getSound("merge.ogg");
        /**
         * The move sound.
         */
        public static final Sound MOVE = getSound("move.ogg");
    }

    /**
     * Disposes of the sound files - should be called at the end of application's lifecycle.
     */
    public static void dispose() {
        Arrays.stream(Sounds.class.getFields())
            .filter(field -> field.getType().equals(Sound.class))
            .forEach(field -> {
                try {
                    Sound s = (Sound) field.get(null);
                    s.dispose();
                } catch (IllegalAccessException e) {
                    // TODO: Handle this properly.
                    throw new RuntimeException(e);
                }
            });
    }
}
