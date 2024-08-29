package pl.kaitou_dev.clone2048.game_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.number_box.BoxColorPalette;
import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.PixmapUtils;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolators;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameGrid implements Disposable {
    private static final int GRID_SIDE = 4;
    private static final int GRID_PADDING = 20;
    private static final int SLOT_SPACING = 10;

    public static final int SIZE = 2 * GRID_PADDING + 4 * Constants.SLOT_SIZE + 3 * SLOT_SPACING;

    private NumberBox[][] grid;
    private ArrayList<NumberBox> boxesToRemove;
    private int secretNumber;
    private Map<Directions, Boolean> movementPossibilities;

    private enum State {
            IDLE, BUSY, GAME_OVER;
    }

    private State state = State.IDLE;


    // Textures & Graphics
    Texture txGridBackground, txGridSlot;
    BoxColorPalette palette = BoxColorPalette.COLORFUL;

    // Geometry
    int posX, posY;

    public GameGrid() {
        grid = new NumberBox[GRID_SIDE][GRID_SIDE];
        boxesToRemove = new ArrayList<>();
        secretNumber = MathNumUtils.randInt(1, 11);
        movementPossibilities = Collections.synchronizedMap(new HashMap<>());
        updateLegalMoves();

        Pixmap pmGridBackground = PixmapUtils.getRoundRectPixmap(SIZE, SIZE, SIZE * 5 / 100, Color.DARK_GRAY);
        txGridBackground = new Texture(pmGridBackground);
        pmGridBackground.dispose();

        Pixmap pmGridSlot = PixmapUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, Color.LIGHT_GRAY);
        txGridSlot = new Texture(pmGridSlot);
        pmGridSlot.dispose();

        addNewBox();
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
        Iterator itBoxesToRemove = boxesToRemove.iterator();
        while (itBoxesToRemove.hasNext()) {
            NumberBox box = (NumberBox) itBoxesToRemove.next();

            if (box.isBusy()) {
                box.update(delta);
                state = State.BUSY;
            }

            else {
                itBoxesToRemove.remove();
                box.dispose();
            }

        }

        // Check if game should end
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

    private void updateLegalMoves() {
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
            case DOWN ->  moveDown();
            case UP -> moveUp();
            case LEFT -> moveLeft();
            case RIGHT -> moveRight();
        }
    }

    private void moveUp() {
        for (int r = 1; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == null) continue;

                grid[r][c] = null;
                int newR = r;

                for (int distance = 1; r - distance >= 0; ++distance) {
                    newR = r - distance;
                    NumberBox otherBox = grid[newR][c];
                    if (otherBox != null) {
                        // Collision
                        if (otherBox.equals(consideredBox)) {
                            otherBox.upgrade();
                            boxesToRemove.add(consideredBox);
                        } else {
                            grid[++newR][c] = consideredBox;
                        }
                        break;
                    } else if (newR == 0) {
                        // Out of bounds
                        grid[newR][c] = consideredBox;
                    }
                }
                Vector2 coords = getSlotCoords(newR, c);
                consideredBox.move((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, Interpolators.QUADRATIC);
            }
        }
    }

    private void moveDown() {
        for (int r = GRID_SIDE - 2; 0 <= r; --r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                // Check the farthest possible movement
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == null) continue;

                grid[r][c] = null;
                int newR = r;

                for (int distance = 1; r + distance < GRID_SIDE; ++distance) {
                    newR = r + distance;
                    NumberBox otherBox = grid[newR][c];
                    if (otherBox != null) {
                        // Collision
                        if (otherBox.equals(consideredBox)) {
                            otherBox.upgrade();
                            boxesToRemove.add(consideredBox);
                        } else {
                            grid[--newR][c] = consideredBox;
                        }
                        break;
                    } else if (newR == GRID_SIDE - 1) {
                        // Out of bounds
                        grid[newR][c] = consideredBox;
                    }
                }
                Vector2 coords = getSlotCoords(newR, c);
                consideredBox.move((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, Interpolators.QUADRATIC);
            }
        }
    }

    private void moveRight() {
        for (int c = GRID_SIDE - 2; 0 <= c; --c) {
            for (int r = 0; r < GRID_SIDE; ++r) {
                // Check the farthest possible movement
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == null) continue;

                grid[r][c] = null;
                int newC = c;

                for (int distance = 1; c + distance < GRID_SIDE; ++distance) {
                    newC = c + distance;
                    NumberBox otherBox = grid[r][newC];
                    if (otherBox != null) {
                        // Collision
                        if (otherBox.equals(consideredBox)) {
                            otherBox.upgrade();
                            boxesToRemove.add(consideredBox);
                        } else {
                            grid[r][--newC] = consideredBox;
                        }
                        break;
                    } else if (newC == GRID_SIDE - 1) {
                        // Out of bounds
                        grid[r][newC] = consideredBox;
                    }
                }
                Vector2 coords = getSlotCoords(r, newC);
                consideredBox.move((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, Interpolators.QUADRATIC);
            }
        }
    }

    private void moveLeft() {
        for (int c = 1; c < GRID_SIDE; ++c) {
            for (int r = 0; r < GRID_SIDE; ++r) {
                // Check the farthest possible movement
                NumberBox consideredBox = grid[r][c];
                if (consideredBox == null) continue;

                grid[r][c] = null;
                int newC = c;

                for (int distance = 1; c - distance >= 0; ++distance) {
                    newC = c - distance;
                    NumberBox otherBox = grid[r][newC];
                    if (otherBox != null) {
                        // Collision
                        if (otherBox.equals(consideredBox)) {
                            otherBox.upgrade();
                            boxesToRemove.add(consideredBox);
                        } else {
                            grid[r][++newC] = consideredBox;
                        }
                        break;
                    } else if (newC == 0) {
                        // Out of bounds
                        grid[r][newC] = consideredBox;
                    }
                }
                Vector2 coords = getSlotCoords(r, newC);
                consideredBox.move((int) coords.x, (int) coords.y, Constants.BASIC_MOVEMENT_SPEED, Interpolators.QUADRATIC);
            }
        }
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

    public NumberBox[] getBound(Directions side) {
        switch (side) {
            case UP -> {
                return grid[0];
            }
            case DOWN -> {
                return grid[GRID_SIDE - 1];
            }
            case LEFT -> {
                return IntStream.range(0, GRID_SIDE).mapToObj(idx -> grid[idx][0]).toArray(NumberBox[]::new);
            }
            case RIGHT -> {
                return IntStream.range(0, GRID_SIDE).mapToObj(idx -> grid[idx][GRID_SIDE - 1]).toArray(NumberBox[]::new);
            }
        }

        // Will never be reached
        return null;
    }

    public NumberBox getNeighbor(int row, int col, Directions side) {
        return switch(side) {
            case DOWN -> (row - 1 >= 0) ? grid[row - 1][col] : null;
            case UP -> (row + 1 < GRID_SIDE) ? grid[row + 1][col] : null;
            case LEFT -> (col - 1 >= 0) ? grid[row][col - 1] : null;
            case RIGHT -> (col + 1 < GRID_SIDE) ? grid[row][col + 1] : null;
            default -> null;
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

    public boolean addNewBox() {
        Vector2 indices = randomEmptyIndices();

        if (indices != null) {
            int r = (int) indices.x;
            int c = (int) indices.y;
            int value = MathNumUtils.diceTest(10, secretNumber) ? 4 : 2;

            NumberBox newBox = new NumberBox(this, value);
            grid[r][c] = newBox;

            Vector2 boxCoords = getSlotCoords(r, c);
            newBox.setCoords((int) boxCoords.x, (int) boxCoords.y);
        }

        return indices != null;
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

        Vector2 choice = emptyIndices.get(MathNumUtils.randInt(emptyIndices.size()));
        return choice;
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

    public void drawBoxes(Batch batch) {
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

    public boolean isGameOver() {
        return state == State.GAME_OVER;
    }
}
