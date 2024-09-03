package pl.kaitou_dev.clone2048;

public class Constants {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final int MAX_WIDTH = 7680;
    public static final int MAX_HEIGHT = 4320;
    public static final float DEFAULT_DELTA = 1f / 60f;
    public static final int SLOT_SIZE = 100;
    public static final float BASIC_MOVEMENT_SPEED = 0.075f;
    public static final int MAX_VALUE = 2048;
    public static final double DEFAULT_BLINK = 0.75;

    public enum GameResult {
        VICTORY("Victory"), GAME_OVER("Game Over");

        private final String resultHeading;

        private GameResult(String resultHeading) {
            this.resultHeading = resultHeading;
        }

        public String getResultHeading() {
            return resultHeading;
        }
    }
}
