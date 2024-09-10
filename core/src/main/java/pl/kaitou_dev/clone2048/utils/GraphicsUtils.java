package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Contains various graphics-related utilities.
 */
public class GraphicsUtils {
    /**
     * A {@link GlyphLayout} instance to be used when dealing with, for example, text and fonts.
     */
    private static final GlyphLayout GLYPH_LAYOUT = new GlyphLayout();

    /**
     * Gets a {@link Pixmap} of a rounded rectangle.
     * @param width The width of the rounded rectangle.
     * @param height The height of the rounded rectangle.
     * @param radius The border radius of the rounded rectangle.
     * @param color The filling color of the rounded rectangle.
     * @return A {@code Pixmap} of the rounded rectangle described by the parameters.
     */
    public static Pixmap getRoundRectPixmap(int width, int height, int radius, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.setFilter(Pixmap.Filter.BiLinear);

        final int xLeft = 0, yBot = 0;
        final int xRight = xLeft + width, yTop = yBot + height;

        // 4 Circles

        pixmap.fillCircle(xLeft + radius, yBot + radius, radius);
        pixmap.fillCircle(xLeft + radius, yTop - radius, radius);
        pixmap.fillCircle(xRight - radius, yBot + radius, radius);
        pixmap.fillCircle(xRight - radius, yTop - radius, radius);

        // 2 Rectangles

        pixmap.fillRectangle(xLeft, yBot + radius, width, height - 2 * radius);
        pixmap.fillRectangle(xLeft + radius, yBot, width - 2 * radius, height);

        return pixmap;
    }

    /**
     * Draws a horizontally-centered line of text onto the specified {@link SpriteBatch}.
     * @param batch The batch to draw onto.
     *              It is required to call {@link SpriteBatch#begin()} sometime before,
     *              and {@link SpriteBatch#end()} sometime after calling this method.
     * @param text The text to be drawn.
     * @param font The font for the text.
     * @param posX The X position of the central point of the text.
     * @param posY The Y position of the central point of the text.
     */
    public static void drawCenteredTextLine(SpriteBatch batch, String text, BitmapFont font, int posX, int posY) {
        GLYPH_LAYOUT.setText(font, text);
        int textWidth = (int) GLYPH_LAYOUT.width;
        int textHeight = (int) GLYPH_LAYOUT.height;
        GLYPH_LAYOUT.reset();

        int x = posX - textWidth / 2;
        int y = posY + textHeight / 2;

        font.draw(batch, text, x, y);
    }
}
