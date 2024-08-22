package pl.kaitou_dev.clone2048;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.MathNumUtils;
import pl.kaitou_dev.clone2048.utils.PixmapUtils;

public class NumberBox {
    private int value;
    private GameGrid grid;

    private int posX, posY;

    private static BitmapFont font;

    private Texture texture;
    private GlyphLayout layout;


    public NumberBox(GameGrid grid, int value) throws IllegalArgumentException {
        if (!MathNumUtils.isPowerOfTwo(value)) throw new IllegalArgumentException("Value must be a power of two");

        this.value = value;
        this.grid = grid;

        texture = new Texture(PixmapUtils.getRoundRectPixmap(Constants.SLOT_SIZE, Constants.SLOT_SIZE, Constants.SLOT_SIZE * 20 / 100, Color.BLUE));
        font = FontUtils.firaCodeRegular(40);
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();
    }

    public void draw(Batch batch) {
        batch.draw(texture, posX, posY);
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

    public String toString() {
        return String.format("[%4s]", value);
    }

    public void setCoords(int x, int y) {
        this.posX = x;
        this.posY = y;
    }
}
