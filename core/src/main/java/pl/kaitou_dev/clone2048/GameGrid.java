package pl.kaitou_dev.clone2048;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import pl.kaitou_dev.clone2048.utils.PixmapUtils;

import java.util.Arrays;

public class GameGrid implements Disposable {
    private static final int GRID_SIDE = 4;
    private static final int GRID_PADDING = 20;
    private static final int SLOT_SPACING = 10;
    private static final int SLOT_SIZE = 100;
    public static final int SIZE = 2 * GRID_PADDING + 4 * SLOT_SIZE + 3 * SLOT_SPACING;

    private NumberBox[][] grid;


    // Textures
    Texture txGridBackground, txGridSlot;

    // Geometry
    int posX, posY;


    public GameGrid() {
        grid = new NumberBox[GRID_SIDE][GRID_SIDE];

        Pixmap pmGridBackground = PixmapUtils.getRoundRectPixmap(SIZE, SIZE, SIZE * 5 / 100, Color.DARK_GRAY);
        txGridBackground = new Texture(pmGridBackground);
        pmGridBackground.dispose();

        Pixmap pmGridSlot = PixmapUtils.getRoundRectPixmap(SLOT_SIZE, SLOT_SIZE, SLOT_SIZE * 20 / 100, Color.LIGHT_GRAY);
        txGridSlot = new Texture(pmGridSlot);
        pmGridSlot.dispose();
    }

    public void drawGrid(SpriteBatch batch, int x, int y) {
        batch.draw(txGridBackground, x, y);
        drawSlots(batch, x, y);
    }

    private void drawSlots(Batch batch, int xLeft, int yBot) {
        for (int r = 0; r < GRID_SIDE; ++r) {
            for (int c = 0; c < GRID_SIDE; ++c) {
                int x = xLeft + GRID_PADDING + (SLOT_SIZE + SLOT_SPACING) * c;
                int y = yBot + SIZE - (GRID_PADDING + SLOT_SPACING * r + SLOT_SIZE * (r + 1));

                batch.draw(txGridSlot, x, y);
            }
        }
    }

    public void drawBoxes(Batch batch) {
        for (NumberBox[] row : grid) {
            for (NumberBox box : row) {
                if (box != null) box.draw(batch);
            }
        }
    }

    @Override
    public void dispose() {
        txGridBackground.dispose();
        txGridSlot.dispose();
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
}
