package pl.kaitou_dev.clone2048.game_entities.number_box;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxAction;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxMoveAction;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.GraphicsUtils;
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
    private GameGrid grid;

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
     * The color palette of this {@code NumberBox}
     */
    private final BoxColorPalette colorPalette;
    /**
     * The background color of this {@code NumberBox}. Its fallback value is sky blue.
     */
    private Color bgColor = Color.SKY;
    /**
     * The font color of this {@code NumberBox}. Its fallback value is black.
     */
    private Color fontColor = Color.BLACK;

    /**
     * The font this {@code NumberBox} is going to use to draw its value.
     */
    private BitmapFont font;

    /**
     * The parameter for scaling this {@code NumberBox}'s size.
     */
    private double scale = 1.0;

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

        colorPalette = this.grid.getPalette();
        updateColors(false);

        texture = new Texture(GraphicsUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, bgColor));

        font = FontUtils.losevka(40);
        updateFontColor();
    }

    /**
     * Draws this {@code NumberBox} onto the specified {@link SpriteBatch}.
     * @param batch The {@code SpriteBatch} onto which to draw.
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, posX, posY, (float) (texture.getWidth() * scale), (float) (texture.getHeight() * scale));
        if (grid.isShouldShowNumbers())
            drawText(batch);
    }

    /**
     * Draws this {@code NumberBox}'s text onto the specified {@link SpriteBatch}.
     * @param batch The {@code SpriteBatch} onto which to draw.
     */
    private void drawText(SpriteBatch batch) {
        int x = posX + Constants.SLOT_SIZE / 2;
        int y = posY + Constants.SLOT_SIZE / 2;
        GraphicsUtils.drawCenteredTextLine(batch, String.valueOf(value), font, x, y);
    }

    /**
     * Upgrades this {@code NumberBox} by making it the next power of 2, and updating its appearance.
     */
    public void upgrade() {
        value <<= 1;
        updateColors(true);
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
        if (!(other instanceof NumberBox)) return false;

        NumberBox otherBox = (NumberBox) other;
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

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

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
     * Updates the color fields of this {@code NumberBox}.
     * @param cascade If this should also update the {@link Texture} and {@link BitmapFont}'s colors.
     */
    private void updateColors(boolean cascade) {
        if (colorPalette != null) {
            bgColor = colorPalette.getColor(value);
            fontColor = colorPalette.getFontColor(bgColor);

            if (cascade) {
                updateTexture();
                updateFontColor();
            }
        }
    }

    /**
     * Disposes of the current {@link Texture} and generates a new one.
     */
    private void updateTexture() {
        if (texture != null) texture.dispose();
        texture = new Texture(GraphicsUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, bgColor));
    }

    /**
     * Updates the {@link BitmapFont}'s font color.
     */
    private void updateFontColor() {
        font.setColor(fontColor);
    }

    /**
     * Disposes of unneeded resources.
     */
    public void dispose() {
        if (texture != null) texture.dispose();
        if (font != null) font.dispose();
    }
}
