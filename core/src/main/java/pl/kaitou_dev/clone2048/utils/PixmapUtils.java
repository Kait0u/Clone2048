package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class PixmapUtils {
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
}
