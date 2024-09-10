package pl.kaitou_dev.clone2048;

/**
 * Contains various constants used throughout the game.
 */
public class Constants {
    /**
     * The width of the resolution at which the game should be rendered.
     */
    public static final int GAME_WIDTH = 800;
    /**
     * The height of the resolution at which the game should be rendered.
     */
    public static final int GAME_HEIGHT = 600;

    /**
     * The max width of the window that displays the game.
     */
    public static final int MAX_WIDTH = 7680;
    /**
     * The max height of the window that displays the game.
     */
    public static final int MAX_HEIGHT = 4320;

    /**
     * The default delta value in case it is not specified.
     */
    public static final float DEFAULT_DELTA = 1f / 60f;

    /**
     * The length of a side of a single slot, in pixels.
     */
    public static final int SLOT_SIZE = 100;

    /**
     * The basic movement speed, in seconds.
     * It should be used to describe how long a movement action should take.
     */
    public static final float BASIC_MOVEMENT_SPEED = 0.075f;

    /**
     * The max value attainable by a box.
     */
    public static final int MAX_VALUE = 2048;

    /**
     * The default duration of a blink, in seconds.
     */
    public static final double DEFAULT_BLINK = 0.75;

    /**
     * An enum containing various states the game can find itself in WHEN FINISHED,
     * paired with {@link String}s that can be used in the UIs.
     */
    public enum GameResult {
        /**
         * Represents a state in which the game is won.
         */
        VICTORY("Victory"),
        /**
         * Represents the state in which the game is lost.
         */
        GAME_OVER("Game Over");

        /**
         * The heading text to be displayed, e.g. on a results screen.
         */
        private final String resultHeading;

        /**
         * The default constructor.
         * @param resultHeading The UI-viable text to be associated with the GameResult.
         */
        private GameResult(String resultHeading) {
            this.resultHeading = resultHeading;
        }

        /**
         * @return The UI-viable heading associated with a given outcome.
         */
        public String getResultHeading() {
            return resultHeading;
        }
    }
}
