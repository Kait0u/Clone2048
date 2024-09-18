package pl.kaitou_dev.clone2048.game_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.number_box.BoxColorPalette;
import pl.kaitou_dev.clone2048.game_entities.number_box.BoxTexturePalette;
import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxScaleAction;
import pl.kaitou_dev.clone2048.utils.*;
import pl.kaitou_dev.clone2048.utils.timed_actions.SequentialAction;
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
    private static final int GRID_PADDING = (int) (Constants.GAME_HEIGHT * 333.0 / 10000.0);

    /**
     * The space between every two neighboring boxes, in pixels.
     */
    private static final int SLOT_SPACING = GRID_PADDING / 2;

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
     * A list of boxes that will be upgraded as soon as events related to them have been handled.
     */
    private final ArrayList<NumberBox> boxesToUpgrade;

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
        /**
         * Signifies a state in which the {@code GameGrid} is not doing anything,
         * and neither are its {@link NumberBox}es.
         */
        IDLE,
        /**
         * Signifies a state in which the {@code GameGrid} is busy processing
         * its {@link NumberBox}es' {@link pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxAction}s
         */
        BUSY,
        /**
         * Signifies a state in which the layout of the grid calls for a game over.
         */
        GAME_OVER,
        /**
         * Signifies a state in which the layout of the grid calls for a victory.
         */
        VICTORY
    }

    /**
     * The current state of the grid, updated whenever some action is performed that could change it.
     */
    private State state = State.IDLE;

    /**
     * A boolean flag dictating whether the {@link NumberBox}es in this grid should display their numbers or not.
     */
    private final boolean shouldShowNumbers;


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
    private final BoxColorPalette colorPalette = BoxColorPalette.COLORFUL;

    /**
     * The texture palette to be used by all of this {@code GameGrid}'s {@link NumberBox}es.
     */
    private final BoxTexturePalette texturePalette;

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
     * Informs whether the sound will be played ({@code true}) or not ({@code false}).
     * The sound is on by default.
     */
    private boolean isSoundOn = true;


    /**
     * A constructor that enables the display of numbers by default.
     */
    public GameGrid() {
        this(true);
    }

    /**
     * A constructor that enables the caller to decide whether this {@code GameGrid}'s {@link NumberBox}es
     * should display their numbers.
     * @param showNumbers Whether the numbers should display (true) or not (false).
     */
    public GameGrid(boolean showNumbers) {
        grid = new NumberBox[GRID_SIDE][GRID_SIDE];
        boxesToRemove = new ArrayList<>();
        boxesToUpgrade = new ArrayList<>();
        secretNumber = MathNumUtils.randInt(1, 11);
        movementPossibilities = Collections.synchronizedMap(new HashMap<>(){{
            for (Directions direction : Directions.values()) {
                put(direction, true);
            }
        }});

        Pixmap pmGridBackground = GraphicsUtils.getRoundRectPixmap(SIZE, SIZE, SIZE * 5 / 100, Color.DARK_GRAY);
        txGridBackground = new Texture(pmGridBackground);
        pmGridBackground.dispose();

        Pixmap pmGridSlot = GraphicsUtils.getRoundRectPixmap(
            Constants.SLOT_SIZE,
            Constants.SLOT_SIZE,
            Constants.SLOT_SIZE * 20 / 100, Color.LIGHT_GRAY
        );
        txGridSlot = new Texture(pmGridSlot);
        pmGridSlot.dispose();

        shouldShowNumbers = showNumbers;

        texturePalette = new BoxTexturePalette(
            colorPalette,
            shouldShowNumbers
                ? FontUtils.losevka(NumberBox.FONT_SIZE)
                : null,
            Constants.MAX_VALUE);

        addNewBox();
        updateLegalMoves();
    }

    /**
     * Prompts this {@code GameGrid}'s {@link NumberBox}es to update and evaluates if the game has to end.
     * @param delta Delta-time at the moment of calling.
     */
    public void update(float delta) {
        State initialState = state;
        state = State.IDLE;

        for (NumberBox[] row : grid) {
            boolean anyBusy = Arrays.stream(row).parallel().filter(Objects::nonNull).anyMatch(box -> {
                box.update(delta);      // Update the box
                return box.isBusy();    // See if it's busy and return the result.
            });
            if (anyBusy) state = State.BUSY;
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

        boolean mergesDone = false;
        // For boxes to upgrade
        if (state.equals(State.IDLE) && !boxesToUpgrade.isEmpty()) {
            boxesToUpgrade.parallelStream().forEach(NumberBox::upgrade);
            boxesToUpgrade.clear();
            mergesDone = true;
            updateLegalMoves();
        }

        // Play a proper sound.
        if (isSoundOn) {
            if (initialState.equals(State.BUSY) && state.equals(State.IDLE)) {
                if (mergesDone) AudioUtils.Sounds.MERGE.play();
                else AudioUtils.Sounds.MOVE.play();
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
            boolean got2048 = isValueOnBoard(Constants.MAX_VALUE);
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
        Set<Integer> directionalKeys = ControlUtils.getDirectionKeys();

        for (Integer gdxKey : directionalKeys) {
            if (Gdx.input.isKeyJustPressed(gdxKey)) {
                move(ControlUtils.getDirection(gdxKey));
                break;
            }
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
     * @see GameGrid#handleMovement(Directions)
     */
    public void move(Directions direction) {
        if (state == State.BUSY) return;

        if (movementPossibilities.get(direction)) {
            handleMovement(direction);
            NumberBox newBox = addNewBox();

            updateLegalMoves();
            state = State.BUSY;
        }
    }

    /**
     * Handles the movement, based on the direction. It establishes the individual {@link NumberBox}es' positions
     * on this grid, and equips them with movement animations.
     * @param direction The direction in which the movement is to take place.
     */
    private void handleMovement(Directions direction) {
        boolean isVertical = direction.isVertical();
        boolean isPositive = direction.isPositive();
        final int distMultiplier = isVertical
            ? (isPositive ? -1 : 1)
            : (isPositive ? 1 : -1);

        IntStream primaryStream = IntStream.range(0, GRID_SIDE);

        if (direction == Directions.DOWN || direction == Directions.RIGHT)
            primaryStream = MathNumUtils.reverseIntStream(primaryStream);

        primaryStream = primaryStream.skip(1);

        primaryStream.forEach(primaryIdx -> {
            IntStream secondaryStream = IntStream.range(0, GRID_SIDE).parallel();
            secondaryStream.forEach(secondaryIdx -> {
                NumberBox consideredBox = isVertical ? grid[primaryIdx][secondaryIdx] : grid[secondaryIdx][primaryIdx];
                if (consideredBox == null) return;

                if (isVertical) grid[primaryIdx][secondaryIdx] = null;
                else grid[secondaryIdx][primaryIdx] = null;

                int newIdx = primaryIdx;

                for (int distance = 1; indexWithinBounds(primaryIdx + distance * distMultiplier); ++distance) {
                    newIdx = primaryIdx + distance * distMultiplier;
                    NumberBox otherBox = isVertical ? grid[newIdx][secondaryIdx] : grid[secondaryIdx][newIdx];

                    if (otherBox != null) {
                        if (otherBox.equals(consideredBox) && !boxesToUpgrade.contains(otherBox)) {
                            boxesToUpgrade.add(otherBox);
                            boxesToRemove.add(consideredBox);
                        } else {
                            newIdx -= distMultiplier;
                            if (isVertical) grid[newIdx][secondaryIdx] = consideredBox;
                            else grid[secondaryIdx][newIdx] = consideredBox;
                        }
                        break;
                    } else if (indexAtBound(newIdx)) {
                        if (isVertical) grid[newIdx][secondaryIdx] = consideredBox;
                        else grid[secondaryIdx][newIdx] = consideredBox;
                    }
                }

                Vector2 coords = isVertical
                    ? getSlotCoords(newIdx, secondaryIdx)
                    : getSlotCoords(secondaryIdx, newIdx);

                consideredBox.actMove((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, DEFAULT_INTERPOLATOR);
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
     * @return {@code true} if movement in the direction is possible, {@code false} if not.
     */
    public boolean isMovementPossible(Directions direction) {
        IntPredicate finalBoundaryPredicate = switch(direction) {
            case DOWN, RIGHT -> (v -> v == GRID_SIDE - 1);
            case UP, LEFT -> (v -> v == 0);
        };

        return IntStream.range(0, GRID_SIDE).parallel().anyMatch(r ->
            IntStream.range(0, GRID_SIDE).parallel().anyMatch(c -> {
                int boundaryTestVal = direction.isVertical() ? r : c;
                boolean boundaryAchieved = finalBoundaryPredicate.test(boundaryTestVal);

                NumberBox consideredBox = grid[r][c];

                // Skip if the current box is empty
                if (consideredBox == null) {
                    return false;
                }

                // Get the neighbor in the specified direction
                NumberBox neighbor = getNeighbor(r, c, direction);

                // Check if movement is possible:
                // 1. The neighbor exists and is empty
                // 2. The neighbor exists and has the same value (merge possible)
                if (!boundaryAchieved && (neighbor == null || neighbor.equals(consideredBox))) {
                    return true;
                }

                return false;
            })
        );
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
            case UP -> indexWithinBounds(row - 1) ? grid[row - 1][col] : null;
            case DOWN -> indexWithinBounds(row + 1) ? grid[row + 1][col] : null;
            case LEFT -> indexWithinBounds(col - 1) ? grid[row][col - 1] : null;
            case RIGHT -> indexWithinBounds(col + 1) ? grid[row][col + 1] : null;
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
     *
     * @return The {@code NumberBox} created, or {@code null} if nothing happened.
     */
    public NumberBox addNewBox() {
        int value = MathNumUtils.diceTest(10, secretNumber) ? 4 : 2;
        return addNewBox(value);
    }

    /**
     * Adds a new {@link NumberBox} of the specified value.
     * @param value The value for the box to have. (Must be a power of 2!)
     * @return The {@code NumberBox} created, or {@code null} if nothing happened.
     */
    public NumberBox addNewBox(int value) {
        Vector2 indices = randomEmptyIndices();

        if (indices != null) {
            int r = (int) indices.x;
            int c = (int) indices.y;

            NumberBox newBox = new NumberBox(this, value);
            grid[r][c] = newBox;

            Vector2 boxCoords = getSlotCoords(r, c);
            newBox.setCoords((int) boxCoords.x, (int) boxCoords.y);

            newBox.setScale(0);
            newBox.setAction(
                new SequentialAction(
                    new BoxScaleAction(newBox, 1.2, Constants.BASIC_MOVEMENT_SPEED * 2 / 3, DEFAULT_INTERPOLATOR),
                    new BoxScaleAction(newBox, 1.0, Constants.BASIC_MOVEMENT_SPEED / 3, DEFAULT_INTERPOLATOR)
                )
            );

            return newBox;
        }

        return null;
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

        texturePalette.dispose();
    }


    /**
     * Gets a String-representation of the current state of this {@code GameGrid}.
     * @return A multi-line String representing the current state of this {@code GameGrid}.
     */
    @Override
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
    public BoxColorPalette getColorPalette() {
        return colorPalette;
    }

    /**
     * Gets the texture palette of this {@code GameGrid}.
     * @return The {@link BoxTexturePalette} of this {@code GameGrid}.
     */
    public BoxTexturePalette getTexturePalette() {
        return texturePalette;
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

    /**
     * Sets the on/off status of sound played by this {@code GameGrid}.
     * @param isOn {@code true} if sounds should play, {@code false} if they should not.
     */
    public void setSoundOn(boolean isOn) {
        isSoundOn = isOn;
    }
}
