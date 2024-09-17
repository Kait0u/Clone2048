package pl.kaitou_dev.clone2048.game_entities.number_box;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxAction;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxMoveAction;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

/**
 * Represents a number box that appears on the screen.
 * It has its own value, which is a power of 2.
 */
public class NumberBox {
    /**
     * The value of the box.
     */
    private int value;

    /**
     * The {@link GameGrid} this {@code NumberBox} belongs to.
     */
    private final GameGrid grid;

    /**
     * The X coordinate of this {@code NumberBox} on the screen.
     */
    private int posX;
    /**
     * The Y coordinate of this {@code NumberBox} on the screen.
     */
    private int posY;

    /**
     * The {@link BoxAction} currently being executed by this {@code NumberBox}.
     */
    private BoxAction action;

    /**
     * The texture of this {@code NumberBox}
     */
    private Texture texture;

    /**
     * The texture palette of this {@code NumberBox}
     */
    private final BoxTexturePalette texturePalette;

    /**
     * The parameter for scaling this {@code NumberBox}'s size.
     */
    private double scale = 1.0;

    /**
     * The recommended font size for the {@code NumberBox}es.
     */
    public static final int FONT_SIZE = Constants.SLOT_SIZE * 40 / 100;

    /**
     * The constructor which takes the parent {@link GameGrid}
     * and the value for this {@code NumberBox} to represent.
     * @param grid The parent {@code GameGrid}.
     * @param value The value, which has to be a power of 2.
     * @throws IllegalArgumentException if the provided value is not a power of 2.
     */
    public NumberBox(GameGrid grid, int value) throws IllegalArgumentException {
        if (!MathNumUtils.isPowerOfTwo(value)) throw new IllegalArgumentException("Value must be a power of two");

        this.value = value;
        this.grid = grid;

        texturePalette = this.grid.getTexturePalette();

        texture = texturePalette.getTexture(value);
    }

    /**
     * Draws this {@code NumberBox} onto the specified {@link SpriteBatch}.
     * @param batch The {@code SpriteBatch} onto which to draw.
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, posX, posY, (float) (texture.getWidth() * scale), (float) (texture.getHeight() * scale));
    }

    /**
     * Upgrades this {@code NumberBox} by making it the next power of 2, and updating its appearance.
     */
    public void upgrade() {
        value <<= 1;
        updateTexture();
    }

    /**
     * Represents a {@code String} representation of this {@code NumberBox}.
     * @return A 6-character {@code String} representing this {@code NumberBox}.
     * <p>e.g {@code "[  2 ]"}.</p>
     */
    @Override
    public String toString() {
        return String.format("[%4s]", value);
    }

    /**
     * Tests for the equality of this {@code NumberBox} and another, based on their values.
     * @param other The other {@code NumberBox}.
     * @return {@code true} if this {@code NumberBox} is equal to another, false if it is not.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof NumberBox otherBox)) return false;

        return value == otherBox.value;
    }

    /**
     * Gets the coordinates of this {@code NumberBox}.
     * @return A {@link Vector2} of this {@code NumberBox}'s coordinates.
     */
    public Vector2 getCoords() {
        return new Vector2(posX, posY);
    }

    /**
     * Sets the coordinates of this {@code NumberBox}.
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     */
    public void setCoords(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * Creates and starts a movement action for this {@code NumberBox}, to a certain point on the screen,
     * within a certain period of time, and with a certain interpolation.
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     * @param durationSeconds The duration of the movement.
     * @param interpolator The {@link Interpolator} used for this movement action.
     */
    public void move(int x, int y, float durationSeconds, Interpolator interpolator) {
        action = new BoxMoveAction(this, x, y, durationSeconds, interpolator);
    }

    /**
     * Gets the value of this {@code NumberBox}.
     * @return The value of this {@code NumberBox}.
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the X coordinate of this {@code NumberBox} on the screen.
     * @return The X coordinate of this {@code NumberBox} on the screen.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Sets the X coordinate of this {@code NumberBox} on the screen.
     * @param posX The new value for the X coordinate.
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Gets the Y coordinate of this {@code NumberBox} on the screen.
     * @return The Y coordinate of this {@code NumberBox} on the screen.
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Sets the Y coordinate of this {@code NumberBox} on the screen.
     * @param posY The new value for the Y coordinate.
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Checks if some {@link BoxAction} is in progress for this {@code NumberBox}.
     * @return {@code true} if this {@code NumberBox} is performing some {@code Action}, {@code false} if not.
     */
    public boolean isBusy() {
        return action != null && !action.isDone();
    }

    /**
     * Updates this {@code NumberBox}'s state and performed {@link BoxAction}, if any, with the current delta time.
     * @param delta The current delta-time.
     */
    public void update(float delta) {
        if (action == null) return;

        action.actWithDelta(delta);

        if (action.isDone())
            action = null;
    }

    /**
     * Gets a new {@link Texture} from its {@link GameGrid}'s {@link BoxTexturePalette}.
     */
    private void updateTexture() {
        texture = texturePalette.getTexture(value);
    }


    /**
     * Disposes of unneeded resources.
     */
    public void dispose() {

    }
}
