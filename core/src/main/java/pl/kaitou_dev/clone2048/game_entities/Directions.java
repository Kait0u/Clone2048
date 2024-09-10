package pl.kaitou_dev.clone2048.game_entities;

/**
 * An enumeration of the four basic directions.
 */
public enum Directions {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Gets the direction opposite to this direction.
     * @return The opposite direction.
     */
    public Directions opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
