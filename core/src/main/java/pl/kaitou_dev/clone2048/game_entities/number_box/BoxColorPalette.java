package pl.kaitou_dev.clone2048.game_entities.number_box;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains multiple colors and a functionality to get different colors, and matching font-colors.
 * It connects a binary power with a specific color.
 */
public class BoxColorPalette {
    /**
     * The standard, colorful color palette.
     */
    public static final BoxColorPalette COLORFUL = new BoxColorPalette() {{
        addColors(
            new Color(0x00a8ffff),
            new Color(0x00ff7fff),
            new Color(0xfbff00ff),
            new Color(0xff8300ff),
            new Color(0xc400ffff),
            new Color(0xff00baff),
            new Color(0x00fffaff),
            new Color(0x11a200ff),
            new Color(0x27009fff),
            new Color(0xff0000ff),
            new Color(0x7b0000ff)
        );
    }};

    /**
     * The threshold for luminance. It serves to establish when a color is too dark to have a black font.
     */
    private static final double LUMINANCE_THRESHOLD = 0.12;

    /**
     * A map which maps binary powers to background colors.
     */
    private final Map<Long, Color> colors;

    /**
     * A map which maps background colors to recommended font colors.
     */
    private final Map<Color, Color> fontColors;

    /**
     * A counter that will become the next power of 2, as more colors are added to a palette.
     * Its initial value is 2, which ensures that the first color corresponds to the number 2.
     */
    private long nextBinaryPower = 2;

    /**
     * The default constructor.
     */
    private BoxColorPalette() {
        colors = new HashMap<>();
        fontColors = new HashMap<>();
    }

    /**
     * A method to add multiple colors to the palette.
     * @param colors Colors to be added to the palette, in order.
     */
    protected void addColors(Color... colors) {
        for (Color color : colors) {
            addColor(color);
        }
    }

    /**
     * Adds a single color to the palette.
     * @param color The color to be added.
     */
    protected void addColor(Color color) {
        colors.put(nextBinaryPower, color);
        nextBinaryPower <<= 1;

        float r = color.r;
        float g = color.g;
        float b = color.b;

        double luminance = calculateLuminance(r, g, b);
        fontColors.put(color, luminance < LUMINANCE_THRESHOLD ? Color.WHITE : Color.BLACK);
    }

    /**
     * Calculates the luminance, for font color matching purposes.
     * @param r Red value - [0; 1].
     * @param g Green value - [0; 1].
     * @param b Blue value  - [0; 1].
     * @return The luminance of the provided RGB color.
     */
    private double calculateLuminance(float r, float g, float b) {
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    /**
     * Gets a color based on a binary power provided.
     * @param binaryPower The binary power, the color corresponding to which to get.
     * @return
     */
    public Color getColor(long binaryPower) {
        return colors.get(binaryPower);
    }

    /**
     * Gets a font color associated with the color.
     * @param color A color from this palette.
     * @return A recommended font color.
     */
    public Color getFontColor(Color color) {
        return fontColors.get(color);
    }

    /**
     * Gets a font color associated with the color associated with the binary power.
     * @param binaryPower A binary power supported by this palette.
     * @return A recommended font color.
     */
    public Color getFontColor(int binaryPower) {
        return fontColors.get(getColor(binaryPower));
    }

}
