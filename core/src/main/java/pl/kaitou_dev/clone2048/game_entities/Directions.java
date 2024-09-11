package pl.kaitou_dev.clone2048.game_entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An enumeration of the four basic directions.
 */
public enum Directions {
    // Vertical
    UP(true, true),
    DOWN(false, true),
    // Horizontal
    LEFT(false, false),
    RIGHT(true, false);

    /**
     * A list of positive directions.
     */
    private static final List<Directions> positiveDirections = new ArrayList<Directions>();
    /**
     * A list of negative directions.
     */
    private static final List<Directions> negativeDirections = new ArrayList<Directions>();
    /**
     * A list of vertical directions.
     */
    private static final List<Directions> verticalDirections = new ArrayList<Directions>();
    /**
     * A list of horizontal directions.
     */
    private static final List<Directions> horizontalDirections = new ArrayList<Directions>();

    static {
        for (Directions direction : Directions.values()) {
            if (direction.isPositive)
                positiveDirections.add(direction);
            else
                negativeDirections.add(direction);

            if (direction.isVertical)
                verticalDirections.add(direction);
            else
                horizontalDirections.add(direction);
        }
    }

    /**
     * A boolean flag indicating that the Direction is positive.
     */
    private boolean isPositive;
    /**
     * A boolean flag indicating that the Direction is vertical (Y-axis).
     */
    private boolean isVertical;

    /**
     * The default constructor, establishing the positiveness and verticality of this direction.
     * @param isPositive If the direction is positive ({@code true} or negative ({@code false}).
     * @param isVertical If the direction is vertical ({@code true} or horizontal ({@code false}).
     */
    private Directions(boolean isPositive, boolean isVertical) {
        this.isPositive = isPositive;
        this.isVertical = isVertical;
    }

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

    /**
     * Evaluates the positiveness of this direction.
     * @return {@code true} if this direction is positive, {@code false} otherwise (negative).
     */
    public boolean isPositive() {
        return isPositive;
    }

    /**
     * Evaluates the verticality of this direction.
     * @return {@code true} if this direction is vertical, {@code false} otherwise (horizontal).
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * Gets positive directions as an unmodifiable {@link List}
     * @return An unmodifiable {@code List} of positive directions.
     */
    public static List<Directions> getPositiveDirections() {
        return Collections.unmodifiableList(positiveDirections);
    }

    /**
     * Gets negative directions as an unmodifiable {@link List}
     * @return An unmodifiable {@code List} of negative directions.
     */
    public static List<Directions> getNegativeDirections() {
        return Collections.unmodifiableList(negativeDirections);
    }

    /**
     * Gets vertical directions as an unmodifiable {@link List}
     * @return An unmodifiable {@code List} of vertical directions.
     */
    public static List<Directions> getVerticalDirections() {
        return Collections.unmodifiableList(verticalDirections);
    }

    /**
     * Gets horizontal directions as an unmodifiable {@link List}
     * @return An unmodifiable {@code List} of horizontal directions.
     */
    public static List<Directions> getHorizontalDirections() {
        return Collections.unmodifiableList(horizontalDirections);
    }
}
