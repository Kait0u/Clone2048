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
    private Texture txGridBackground, txGridSlot;
    private BoxColorPalette palette = BoxColorPalette.COLORFUL;

    // Geometry
    private int posX, posY;

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

    public GameGrid(boolean showNumbers) {
        this();
        shouldShowNumbers = showNumbers;
    }

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

    private void handleVictoryLoss() {
        if (state == State.IDLE) {
            boolean got2048 = isValueOnBoard(2048);
            if (got2048) state = State.VICTORY;
        }

        if (state == State.IDLE) {
            boolean anyMovementPossible = movementPossibilities.values().stream().anyMatch(Boolean::booleanValue);
            if (!anyMovementPossible) state = State.GAME_OVER;
        }
    }

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

    public void updateLegalMoves() {
        Arrays.stream(Directions.values()).parallel()
            .forEach(direction -> movementPossibilities.put(direction, isMovementPossible(direction)));
    }

    public void handleMovement(Directions direction) {
        if (state == State.BUSY) return;

        if (movementPossibilities.get(direction)) {
            move(direction);
            addNewBox();
            updateLegalMoves();
        }
    }

    public void move(Directions direction) {
        switch (direction) {
            case UP, DOWN -> moveVertically(direction);
            case LEFT, RIGHT -> moveHorizontally(direction);
        }
    }


    private void moveVertically(Directions direction) {
        final int distMultiplier = switch (direction) {
            case UP -> -1;
            case DOWN -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
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

    private void moveHorizontally(Directions direction) {
        final int distMultiplier = switch (direction) {
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
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

    private boolean indexWithinBounds(int idx) {
        return 0 <= idx && idx < GRID_SIDE;
    }

    private boolean indexAtBound(int idx) {
        return idx == 0 || idx == GRID_SIDE - 1;
    }

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

    public NumberBox getNeighbor(int row, int col, Directions side) {
        return switch(side) {
            case DOWN -> (row - 1 >= 0) ? grid[row - 1][col] : null;
            case UP -> (row + 1 < GRID_SIDE) ? grid[row + 1][col] : null;
            case LEFT -> (col - 1 >= 0) ? grid[row][col - 1] : null;
            case RIGHT -> (col + 1 < GRID_SIDE) ? grid[row][col + 1] : null;
        };
    }

    public HashMap<Directions, NumberBox> getNeighbors(int row, int col) {
        HashMap<Directions, NumberBox> neighbors = new HashMap<>();

        for (Directions direction: Directions.values())
            neighbors.put(direction, getNeighbor(row, col, direction));

        return neighbors;
    }

    public Vector2 getIndices(NumberBox box) {
        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == box) return new Vector2(r, c);
            }
        }

        return null;
    }

    public void addNewBox() {
        int value = MathNumUtils.diceTest(10, secretNumber) ? 4 : 2;
        addNewBox(value);
    }

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

    private Vector2 randomEmptyIndices() {
        ArrayList<Vector2> emptyIndices = getEmptyIndices();
        if (emptyIndices.isEmpty()) return null;

        return emptyIndices.get(MathNumUtils.randInt(emptyIndices.size()));
    }

    public void drawGrid(SpriteBatch batch) {
        batch.draw(txGridBackground, posX, posY);
        drawSlots(batch);
    }

    private void drawSlots(Batch batch) {
        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                Vector2 coords = getSlotCoords(r, c);

                int x = (int) coords.x, y = (int) coords.y;
                batch.draw(txGridSlot, x, y);
            }
        }
    }

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

    public Vector2 getSlotCoords(int row, int col) {
        int x = posX + GRID_PADDING + (Constants.SLOT_SIZE + SLOT_SPACING) * col;
        int y = posY + SIZE - (GRID_PADDING + SLOT_SPACING * row + Constants.SLOT_SIZE * (row + 1));

        return new Vector2(x, y);
    }

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

    public void setCoords(int x, int y) {
        posX = x;
        posY = y;

        updateBoxCoords();
    }

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

    public BoxColorPalette getPalette() {
        return palette;
    }

    public boolean isValueOnBoard(int value) {
        if (!MathNumUtils.isPowerOfTwo(value)) return false;

        return Stream.of(grid).flatMap(Stream::of).filter(Objects::nonNull).anyMatch(box -> box.getValue() == value);
    }

    public boolean isGameOver() {
        return state == State.GAME_OVER;
    }

    public boolean isVictory() {
        return state == State.VICTORY;
    }

    public boolean isBusy() {
        return state == State.BUSY;
    }

    public State getState() {
        return state;
    }

    public boolean isShouldShowNumbers() {
        return shouldShowNumbers;
    }
}
