package pl.kaitou_dev.clone2048.game_entities.number_box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.FontUtils;
import pl.kaitou_dev.clone2048.utils.GraphicsUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generates and then serves {@link com.badlogic.gdx.graphics.Texture}s for {@link NumberBox}es,
 * associating said {@code Texture}s with consecutive binary powers.
 */
public class BoxTexturePalette extends BinaryPalette<Texture> implements Disposable {
    private final BoxColorPalette colorPalette;
    private final BitmapFont font;
    private final boolean shouldDisplayText;

    private final Map<Long, Texture> textureMap;

    /**
     * The default constructor. It sets this palette up, but does not generate any {@link Texture}s.
     * @param colorPalette The color palette to use for future {@code Texture}s.
     * @param font The font to use for future {@code Texture}s. If {@code null}, then no text will be rendered.
     */
    public BoxTexturePalette(BoxColorPalette colorPalette, BitmapFont font) {
        super();

        this.colorPalette = colorPalette;
        this.font = font;
        this.shouldDisplayText = (font != null);
        this.textureMap = super.items;
    }

    /**
     * An alternative constructor. It sets this palette up, and also generates {@link Texture}s,
     * associating them with consecutive binary powers that are less than or equal to a provided maximum.
     * @param colorPalette The color palette to use for {@code Texture}s.
     * @param font The font to use for generated {@code Texture}s. If {@code null}, then no text will be rendered.
     * @param maxInclusive The maximum value, which generated binary powers will not exceed.
     *                     It is an inclusive boundary.
     * @see #generateTextures(long)

     */
    public BoxTexturePalette(BoxColorPalette colorPalette, BitmapFont font, long maxInclusive) {
        this(colorPalette, font);
        generateTextures(maxInclusive);
    }

    /**
     * Generates {@link Texture}s for consecutive binary powers less than or equal to the provided number.
     * @param maxInclusive The maximum value, which generated binary powers will not exceed.
     *                     It is an inclusive boundary.
     */
    public void generateTextures(long maxInclusive) {
        clear();

        for (long binPow = 2; binPow <= maxInclusive; binPow <<= 1) {
            addItem(createTexture(binPow));
        }
    }

    /**
     * Creates a texture corresponding to the provided binary power.
     * @param binPow The binary power to create a {@link Texture} for.
     * @return A {@code Texture} for the provided binary power, or {@code null} if the number was not a binary power,
     *         or exceeded used palettes' maximum binary powers.
     */
    private Texture createTexture(long binPow) {
        Color bgColor = colorPalette.getColor(binPow);
        if (bgColor == null) return null;

        Color fontColor = colorPalette.getFontColor(bgColor);
        Pixmap boxBgPixmap = GraphicsUtils.getRoundRectPixmap(
            Constants.SLOT_SIZE,
            Constants.SLOT_SIZE,
            Constants.SLOT_SIZE * 20 / 100,
            bgColor
        );

        return shouldDisplayText
            ? createTextureWithText(boxBgPixmap, binPow, fontColor)
            : new Texture(boxBgPixmap);
    }

    /**
     * A helper method for {@link #createTexture(long)}. Creates a {@link Texture} using the provided {@link Pixmap},
     * binary power and font color.
     * @param bg The {@code Pixmap} to serve as a background.
     * @param binPow The binary power to render on the {@code Texture} as text.
     * @param fontColor The color for the font.
     * @return A {@code Texture} for the provided binary power, with text rendered onto it.
     */
    private Texture createTextureWithText(Pixmap bg, long binPow, Color fontColor) {
        font.setColor(fontColor);

        FrameBuffer fb = new FrameBuffer(
            Pixmap.Format.RGBA8888,
            Constants.SLOT_SIZE,
            Constants.SLOT_SIZE,
            true
        );


        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true, Constants.SLOT_SIZE, Constants.SLOT_SIZE);

        SpriteBatch batch = new SpriteBatch();

        Texture tempTx = new Texture(bg);
        fb.begin();

        Gdx.gl.glClearColor(1, 1,1 ,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        batch.draw(tempTx, 0, 0);
        GraphicsUtils.drawCenteredTextLine(
            batch,
            String.valueOf(binPow),
            font,
            Constants.SLOT_SIZE / 2,
            Constants.SLOT_SIZE / 2
        );

        batch.end();

        Pixmap fbPixmap = Pixmap.createFromFrameBuffer(0, 0, Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        fb.end();

        Texture result = new Texture(fbPixmap);

        fb.dispose();
        fbPixmap.dispose();
        batch.dispose();

        return result;
    }

    /**
     * Gets a {@link Texture} based on the provided binary power.
     * @param binPow The binary power to get a {@code Texture} associated with.
     * @return A {@code Texture} associated with the provided binary power,
     *         or {@code null} if there is no {@code Texture} associated with such a power.
     * @throws IllegalArgumentException if the provided number is not a binary power.
     */
    public Texture getTexture(long binPow) {
        return getItem(binPow);
    }

    @Override
    public void dispose() {
        textureMap.values().forEach(Texture::dispose);
        if (font != null) font.dispose();
    }
}
