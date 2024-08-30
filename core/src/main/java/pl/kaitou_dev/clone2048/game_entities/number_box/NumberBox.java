package pl.kaitou_dev.clone2048.game_entities.number_box;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.GameGrid;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxAction;
import pl.kaitou_dev.clone2048.game_entities.number_box.actions.BoxMoveAction;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.PixmapUtils;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

public class NumberBox {
    private int value;
    private GameGrid grid;


    private int posX, posY;
    private BoxAction action;

    private BitmapFont font;

    private Texture texture;
    private BoxColorPalette colorPalette;
    private Color bgColor = Color.SKY;
    private Color fontColor = Color.BLACK;

    private GlyphLayout layout;


    private double scale = 1.0;


    public NumberBox(GameGrid grid, int value) throws IllegalArgumentException {
        if (!MathNumUtils.isPowerOfTwo(value)) throw new IllegalArgumentException("Value must be a power of two");

        this.value = value;
        this.grid = grid;

        colorPalette = this.grid.getPalette();
        updateColors(false);

        texture = new Texture(PixmapUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, bgColor));

        font = FontUtils.losevka(40);
        updateFontColor();

        layout = new GlyphLayout();
    }


    public void draw(Batch batch) {
        batch.draw(texture, posX, posY, (float) (texture.getWidth() * scale), (float) (texture.getHeight() * scale));
        drawText(batch);
    }

    private void drawText(Batch batch) {
        layout.setText(font, String.valueOf(value));
        int textWidth = (int) layout.width;
        int textHeight = (int) layout.height;

        int x = posX + Constants.SLOT_SIZE / 2 - textWidth / 2;
        int y = posY + Constants.SLOT_SIZE / 2 + textHeight / 2;

        font.draw(batch, String.valueOf(value), x, y);
    }

    public void upgrade() {
        value <<= 1;
        updateColors(true);
    }

    @Override
    public String toString() {
        return String.format("[%4s]", value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof NumberBox)) return false;

        NumberBox otherBox = (NumberBox) other;
        return value == otherBox.value;
    }

    public Vector2 getCoords() {
        return new Vector2(posX, posY);
    }

    public void setCoords(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void moveLinear(int x, int y, float durationSeconds) {
        action = new BoxMoveAction(this, x, y, durationSeconds);
    }

    public void move(int x, int y, float durationSeconds, Interpolator interpolator) {
        action = new BoxMoveAction(this, x, y, durationSeconds, interpolator);
    }

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

    public boolean isBusy() {
        return action != null && !action.isDone();
    }

    public void update(float delta) {
        if (action == null) return;

        action.actWithDelta(delta);

        if (action.isDone())
            action = null;
    }

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

    private void updateTexture() {
        if (texture != null) texture.dispose();
        texture = new Texture(PixmapUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, bgColor));
    }

    private void updateFontColor() {
        font.setColor(fontColor);
    }


    public void dispose() {
        if (texture != null) texture.dispose();
        if (font != null) font.dispose();
    }
}
