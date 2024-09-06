package pl.kaitou_dev.clone2048.game_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.number_box.BoxColorPalette;
import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.GraphicsUtils;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolators;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A {@code GameGrid} contains the current state of the game board, with all of its {@link NumberBox}es.
 * It contains methods for manipulating the current state of the game, control-handling and establishing
 * the status of the current round.
 * @see NumberBox
 */
public class GameGrid implements Disposable {
    /**
     * The length of one side of the square grid the game takes place on, in NumberBoxes.
     */
    private static final int GRID_SIDE = 4;

    /**
     * The padding of the grid, in pixels.
     */
    private static final int GRID_PADDING = 20;

    /**
     * The space between every two neighboring boxes, in pixels.
     */
    private static final int SLOT_SPACING = 10;

    /**
     * The length of one side of the grid, in pixels.
     */
    public static final int SIZE = 2 * GRID_PADDING + 4 * Constants.SLOT_SIZE + 3 * SLOT_SPACING;

    /**
     * The interpolator for various animations and activities over-time.
     */
    public static final Interpolator DEFAULT_INTERPOLATOR = Interpolators.QUADRATIC;

    /**
     * A 2D Array representation of the current layout of {@link NumberBox}es.
     */
    private final NumberBox[][] grid;

    /**
     * A list of boxes that will be removed as soon as events related to them have been handled.
     */
    private final ArrayList<NumberBox> boxesToRemove;

    /**
     * A secret number that will allow for dice tests.
     */
    private final int secretNumber;

    /**
     * A map that describes whether a movement is possible in a certain direction, using a boolean flag.
     * @see Directions
     */
    private final Map<Directions, Boolean> movementPossibilities;

    /**
     * An enum of the states the game can find itself in, based on the {@link NumberBox} activity,
     * as well as the layout on the grid.
     */
    public enum State {
        IDLE, BUSY, GAME_OVER, VICTORY
    }

    /**
     * The current state of the grid, updated whenever some action is performed that could change it.
     */
    private State state = State.IDLE;

    /**
     * A boolean flag dictating whether the {@link NumberBox}es in this grid should display their numbers or not.
     */
    private boolean shouldShowNumbers = true;


    // Textures & Graphics
    /**
     * The texture used to display the background of the grid.
     */
    private final Texture txGridBackground;

    /**
     * The texture used to display each slot of the grid.
     */
    private final Texture txGridSlot;

    /**
     * The color palette to be used by all of this {@code GameGrid}'s {@link NumberBox}es.
     */
    private final BoxColorPalette palette = BoxColorPalette.COLORFUL;

    // Geometry

    /**
     * Measures the position of the bottom-left corner's X coordinate.
     */
    private int posX;

    /**
     * Measures the position of the bottom-left corner's Y coordinate.
     */
    private int posY;

    /**
     * The default constructor.
     */
    public GameGrid() {
        grid = new NumberBox[GRID_SIDE][GRID_SIDE];
        boxesToRemove = new ArrayList<>();
        secretNumber = MathNumUtils.randInt(1, 11);
        movementPossibilities = Collections.synchronizedMap(new HashMap<>());
        updateLegalMoves();

        Pixmap pmGridBackground = GraphicsUtils.getRoundRectPixmap(SIZE, SIZE, SIZE * 5 / 100, Color.DARK_GRAY);
        txGridBackground = new Texture(pmGridBackground);
        pmGridBackground.dispose();

        Pixmap pmGridSlot = GraphicsUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, Color.LIGHT_GRAY);
        txGridSlot = new Texture(pmGridSlot);
        pmGridSlot.dispose();

        addNewBox();
    }

    /**
     * An alternate constructor that enables the caller to decide whether this {@code GameGrid}'s {@link NumberBox}es
     * should display their numbers.
     * @param showNumbers Whether the numbers should display (true) or not (false).
     */
    public GameGrid(boolean showNumbers) {
        this();
        shouldShowNumbers = showNumbers;
    }

    /**
     * Prompts this {@code GameGrid}'s {@link NumberBox}es to update and evaluates if the game has to end.
     * @param delta Delta-time at the moment of calling.
     */
    public void update(float delta) {
        state = State.IDLE;

        for (NumberBox[] row : grid) {
            for (NumberBox box : row) {
                if (box != null) {
                    box.update(delta);
                    if (box.isBusy()) state = State.BUSY;
                }
            }
        }

        // For boxes to remove
        Iterator<NumberBox> itBoxesToRemove = boxesToRemove.iterator();
        while (itBoxesToRemove.hasNext()) {
            NumberBox box = itBoxesToRemove.next();

            if (box.isBusy()) {
                box.update(delta);
                state = State.BUSY;
            } else {
                itBoxesToRemove.remove();
                box.dispose();
            }

        }

        // Check if game should end
        handleVictoryLoss();
    }

    /**
     * Evaluates if the game has to end, and with what result, and updates the state accordingly.
     * @see State
     */
    private void handleVictoryLoss() {
        if (state == State.IDLE) {
            // Check for victory.
            boolean got2048 = isValueOnBoard(2048);
            if (got2048) {
                state = State.VICTORY;
                return;
            }

            // Check for loss.
            boolean anyMovementPossible = movementPossibilities.values().stream().anyMatch(Boolean::booleanValue);
            if (!anyMovementPossible) state = State.GAME_OVER;
        }
    }


    /**
     * Handles the input related to this {@code GameGrid}.
     */
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            handleMovement(Directions.UP);
        }

        else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            handleMovement(Directions.DOWN);
        }

        else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            handleMovement(Directions.LEFT);
        }

        else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            handleMovement(Directions.RIGHT);
        }
    }

    /**
     * Updates the map of movement possibilities based on the current situation on this {@code GameGrid}.
     */
    public void updateLegalMoves() {
        Arrays.stream(Directions.values()).parallel()
            .forEach(direction -> movementPossibilities.put(direction, isMovementPossible(direction)));
    }

    /**
     * Handles the movement on this {@code GameGrid}.
     * Does nothing if a move in the provided direction is impossible, or a movement is already in progress.
     * Updates the map of movement possibilities upon each successful move.
     * @param direction The direction in which the movement is to take place.
     */
    public void handleMovement(Directions direction) {
        if (state == State.BUSY) return;

        if (movementPossibilities.get(direction)) {
            move(direction);
            addNewBox();
            updateLegalMoves();
            state = State.BUSY;
        }
    }

    /**
     * Handles the movement by dispatching a proper method - based on whether the movement is vertical or horizontal.
     * @param direction The direction in which the movement is to take place.
     */
    private void move(Directions direction) {
        switch (direction) {
            case UP, DOWN -> moveVertically(direction);
            case LEFT, RIGHT -> moveHorizontally(direction);
        }
    }

    /**
     * Handles vertical movement. Fails if the provided direction is not vertical.
     * @param direction A vertical direction.
     * @throws IllegalArgumentException if the provided direction was not vertical.
     */
    private void moveVertically(Directions direction) {
        final int distMultiplier = switch (direction) {
            case UP -> -1;
            case DOWN -> 1;
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        };

        IntStream rowIdxStream = (direction == Directions.DOWN
            ? MathNumUtils.reverseIntStream(IntStream.range(0, GRID_SIDE))
            : IntStream.range(0, GRID_SIDE));

        rowIdxStream = rowIdxStream.skip(1);

        rowIdxStream.forEach(r -> {
            IntStream colIdxStream = IntStream.range(0, GRID_SIDE);
            colIdxStream.forEach(c -> {
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == null) return;

                grid[r][c] = null;
                int newR = r;

                for (int distance = 1; indexWithinBounds(r + distance * distMultiplier); ++distance) {
                    newR = r + distance * distMultiplier;
                    NumberBox otherBox = grid[newR][c];

                    if (otherBox != null) {
                        if (otherBox.equals(consideredBox)) {
                            otherBox.upgrade();
                            boxesToRemove.add(consideredBox);
                        } else {
                            newR -= distMultiplier;
                            grid[newR][c] = consideredBox;
                        }
                        break;
                    } else if (indexAtBound(newR)) {
                        grid[newR][c] = consideredBox;
                    }
                }

                Vector2 coords = getSlotCoords(newR, c);
                consideredBox.move((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, DEFAULT_INTERPOLATOR);
            });
        });

    }

    /**
     * Handles horizontal movement. Fails if the provided direction is not horizontal.
     * @param direction A horizontal direction.
     * @throws IllegalArgumentException if the provided direction was not horizontal.
     */
    private void moveHorizontally(Directions direction) {
        final int distMultiplier = switch (direction) {
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        };

        IntStream colIdxStream = direction == Directions.RIGHT
            ? MathNumUtils.reverseIntStream(IntStream.range(0, GRID_SIDE))
            : IntStream.range(0, GRID_SIDE);

        colIdxStream = colIdxStream.skip(1);

        colIdxStream.forEach(c -> {
            IntStream rowIdxStream = IntStream.range(0, GRID_SIDE);
            rowIdxStream.forEach(r -> {
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == null) return;

                grid[r][c] = null;
                int newC = c;

                for (int distance = 1; indexWithinBounds(c + distance * distMultiplier); ++distance) {
                    newC = c + distance * distMultiplier;
                    NumberBox otherBox = grid[r][newC];
                    if (otherBox != null) {
                        // Collision
                        if (otherBox.equals(consideredBox)) {
                            otherBox.upgrade();
                            boxesToRemove.add(consideredBox);
                        } else {
                            newC -= distMultiplier;
                            grid[r][newC] = consideredBox;
                        }
                        break;
                    } else if (indexAtBound(newC))
                        grid[r][newC] = consideredBox;
                }
                Vector2 coords = getSlotCoords(r, newC);
                consideredBox.move((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, DEFAULT_INTERPOLATOR);
            });
        });
    }

    /**
     * Checks if the provided index is within this {@code GameGrid}'s bounds.
     * @param idx The index to be tested.
     * @return {@code true} if the index is within the bounds, {@code false} if not.
     */
    private boolean indexWithinBounds(int idx) {
        return 0 <= idx && idx < GRID_SIDE;
    }

    /**
     * Checks if the provided index is at one of this {@code GameGrid}'s bounds.
     * @param idx The index to be tested.
     * @return {@code true} if the index is at one of the bounds, {@code false} if not.
     */
    private boolean indexAtBound(int idx) {
        return idx == 0 || idx == GRID_SIDE - 1;
    }

    /**
     * Evaluates whether a movement in the provided direction is possible or not.
     * @param direction The direction whose movement's possibility is to be evaluated.
     * @return {@code true} if such movement in the direction is possible, {@code false} if not.
     */
    public boolean isMovementPossible(Directions direction) {
        IntPredicate boundaryPredicate = switch(direction) {
            case UP, LEFT -> (v -> v < GRID_SIDE - 1);
            case DOWN, RIGHT -> (v -> v > 0);
        };

        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                NumberBox consideredBox = grid[r][c];
                NumberBox neighbor = getNeighbor(r, c, direction);

                int boundaryTestVal = switch (direction) {
                    case UP, DOWN -> r;
                    case LEFT, RIGHT -> c;
                };

                if (consideredBox == null) {
                    if (boundaryPredicate.test(boundaryTestVal)) return true;
                } else if (consideredBox.equals(neighbor)) return true;
            }
        }

        return false;
    }

    /**
     * Gets the contents of a neighbor-slot of the slot that is described by the pair of indices.
     * @param row The row index.
     * @param col The column index.
     * @param side The direction of the neighbor, relative to the slot given by the row-column coordinates.
     * @return The neighboring {@link NumberBox} or null, if the neighbor slot is empty.
     */
    public NumberBox getNeighbor(int row, int col, Directions side) {
        return switch(side) {
            case DOWN -> (row - 1 >= 0) ? grid[row - 1][col] : null;
            case UP -> (row + 1 < GRID_SIDE) ? grid[row + 1][col] : null;
            case LEFT -> (col - 1 >= 0) ? grid[row][col - 1] : null;
            case RIGHT -> (col + 1 < GRID_SIDE) ? grid[row][col + 1] : null;
        };
    }

    /**
     * Gets all 4 neighbors of a slot given by
     * @param row The row index.
     * @param col The column index.
     * @return {@link Directions}-to-neighbor map.
     * @see #getNeighbor(int, int, Directions)
     */
    public HashMap<Directions, NumberBox> getNeighbors(int row, int col) {
        HashMap<Directions, NumberBox> neighbors = new HashMap<>();

        for (Directions direction: Directions.values())
            neighbors.put(direction, getNeighbor(row, col, direction));

        return neighbors;
    }

    /**
     * Returns the indices of the provided {@link NumberBox} in this {@code GameGrid}.
     * @param box The {@code NumberBox} to look for.
     * @return A {@code Vector2} consisting of the row- and column indices,
     * or {@code null} if the box was not found in this grid.
     */
    public Vector2 getIndices(NumberBox box) {
        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == box) return new Vector2(r, c);
            }
        }

        return null;
    }

    /**
     * Adds a new {@link NumberBox} to this {@code GameGrid}.
     * There is 90% chance that the box will have the value of 2, and 10% chance that it will have the value of 4.
     */
    public void addNewBox() {
        int value = MathNumUtils.diceTest(10, secretNumber) ? 4 : 2;
        addNewBox(value);
    }

    /**
     * Adds a new {@link NumberBox} of the specified value.
     * @param value The value for the box to have. (Must be a power of 2!)
     */
    public void addNewBox(int value) {
        Vector2 indices = randomEmptyIndices();

        if (indices != null) {
            int r = (int) indices.x;
            int c = (int) indices.y;

            NumberBox newBox = new NumberBox(this, value);
            grid[r][c] = newBox;

            Vector2 boxCoords = getSlotCoords(r, c);
            newBox.setCoords((int) boxCoords.x, (int) boxCoords.y);
        }

    }

    /**
     * Gets a list of {@code Vector2}s representing coordinates of all empty slots in this {@code GameGrid}.
     * @return A list of slot-indices without any box inside of them.
     */
    private ArrayList<Vector2> getEmptyIndices() {
        ArrayList<Vector2> emptyIndices = new ArrayList<>();

        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                if (grid[r][c] == null)
                    emptyIndices.add(new Vector2(r, c));
            }
        }

        return emptyIndices;
    }

    /**
     * Gets a pair of indices for a random empty slot in this {@code GameGrid}.
     * @return A {@code Vector2} of indices for a random empty slot, or {@code null} if the grid has no empty slots.
     */
    private Vector2 randomEmptyIndices() {
        ArrayList<Vector2> emptyIndices = getEmptyIndices();
        if (emptyIndices.isEmpty()) return null;

        return emptyIndices.get(MathNumUtils.randInt(emptyIndices.size()));
    }

    /**
     * Draws the grid along with its slots.
     * @param batch The batch used in the current rendering process.
     */
    public void drawGrid(SpriteBatch batch) {
        batch.draw(txGridBackground, posX, posY);
        drawSlots(batch);
    }

    /**
     * Draws the slots of the grid.
     * @param batch The batch used in the current rendering process.
     */
    private void drawSlots(Batch batch) {
        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                Vector2 coords = getSlotCoords(r, c);

                int x = (int) coords.x, y = (int) coords.y;
                batch.draw(txGridSlot, x, y);
            }
        }
    }

    /**
     * Prompts all of this {@code GameGrid}'s {@link NumberBox}es to be drawn.
     * @param batch The batch used in the current rendering process.
     */
    public void drawBoxes(SpriteBatch batch) {
        for (NumberBox box: boxesToRemove) {
            box.draw(batch);
        }

        for (NumberBox[] row : grid) {
            for (NumberBox box : row) {
                if (box != null) box.draw(batch);
            }
        }
    }

    /**
     * Gets the X, Y coordinates of the slot given by row, column indices.
     * @param row Row-index.
     * @param col Column-index.
     * @return A {@code Vector2} of X, Y coordinates for the designated slot.
     */
    public Vector2 getSlotCoords(int row, int col) {
        int x = posX + GRID_PADDING + (Constants.SLOT_SIZE + SLOT_SPACING) * col;
        int y = posY + SIZE - (GRID_PADDING + SLOT_SPACING * row + Constants.SLOT_SIZE * (row + 1));

        return new Vector2(x, y);
    }

    /**
     * Disposes of this {@code GameGrid}'s resources, and of its {@link NumberBox}es' resources as well.
     */
    @Override
    public void dispose() {
        txGridBackground.dispose();
        txGridSlot.dispose();
        for (NumberBox[] row : grid) {
            for (NumberBox box : row) {
                if (box != null) box.dispose();
            }
        }

        for (NumberBox box : boxesToRemove) {
            box.dispose();
        }
    }


    /**
     * Gets a String-representation of the current state of this {@code GameGrid}.
     * @return A multi-line String representing the current state of this {@code GameGrid}.
     */
    public String toString() {
        String nullString = "[    ]";
        StringBuilder sb = new StringBuilder();
        for (NumberBox[] row : grid) {
            for (NumberBox box : row) {
                sb.append(box == null ? nullString : box.toString());
                sb.append(' ');
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Sets the current X, Y coordinates,
     * so that this {@code GameGrid} can provide its {@link NumberBox}es
     * with appropriate coordinates.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void setCoords(int x, int y) {
        posX = x;
        posY = y;

        updateBoxCoords();
    }

    /**
     * Updates all of this {@code GameGrid}'s {@link NumberBox}es' X, Y coordinates.
     */
    private void updateBoxCoords() {
        IntStream.range(0, GRID_SIDE).parallel().forEach(r -> {
            IntStream.range(0, GRID_SIDE).parallel().forEach(c -> {
                NumberBox box = grid[r][c];
                if (box == null) return;

                Vector2 slotCoords = getSlotCoords(r, c);
                box.setCoords((int) slotCoords.x, (int) slotCoords.y);
            });
        });
    }

    /**
     * Gets the color palette of this {@code GameGrid}.
     * @return The {@link BoxColorPalette} of this {@code GameGrid}.
     */
    public BoxColorPalette getPalette() {
        return palette;
    }

    /**
     * Checks if the provided value exists on the board.
     * @param value The value to be checked.
     * @return {@code true} if such a value exists on the board, {@code false} if not.
     */
    public boolean isValueOnBoard(int value) {
        if (!MathNumUtils.isPowerOfTwo(value)) return false;

        return Stream.of(grid).flatMap(Stream::of).filter(Objects::nonNull).anyMatch(box -> box.getValue() == value);
    }

    /**
     * Checks if this {@code GameGrid}'s layout indicates a loss.
     * @return {@code true} if the game is lost, {@code false} if not.
     */
    public boolean isGameOver() {
        return state == State.GAME_OVER;
    }

    /**
     * Checks if this {@code GameGrid}'s layout indicates a victory.
     * @return {@code true} if the game is won, {@code false} if not.
     */
    public boolean isVictory() {
        return state == State.VICTORY;
    }

    /**
     * Checks if this {@code GameGrid} is (its {@link NumberBox}es are) busy.
     * @return {@code true} if busy, {@code false} if not.
     */
    public boolean isBusy() {
        return state == State.BUSY;
    }

    /**
     * Gets the current state of this {@code GameGrid}.
     * @return The current state of this {@code GameGrid}.
     * @see State
     */
    public State getState() {
        return state;
    }

    /**
     * Checks if this {@code GameGrid}'s {@link NumberBox}es should display their numbers.
     * @return {@code true} if the boxes should display their numbers, {@code false} if not.
     */
    public boolean isShouldShowNumbers() {
        return shouldShowNumbers;
    }
}
