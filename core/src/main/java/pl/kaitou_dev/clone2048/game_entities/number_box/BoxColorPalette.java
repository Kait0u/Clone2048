package pl.kaitou_dev.clone2048.game_entities.number_box;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class BoxColorPalette {
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

    private static final double LUMINANCE_THRESHOLD = 0.12;

    private final Map<Long, Color> colors;
    private final Map<Color, Color> fontColors;
    private long nextBinaryPower = 2;

    private BoxColorPalette() {
        colors = new HashMap<>();
        fontColors = new HashMap<>();
    }

    protected void addColors(Color... colors) {
        for (Color color : colors) {
            addColor(color);
        }
    }

    protected void addColor(Color color) {
        colors.put(nextBinaryPower, color);
        nextBinaryPower <<= 1;

        float r = color.r;
        float g = color.g;
        float b = color.b;

        double luminance = calculateLuminance(r, g, b);
        fontColors.put(color, luminance < LUMINANCE_THRESHOLD ? Color.WHITE : Color.BLACK);
    }

    private double calculateLuminance(float r, float g, float b) {
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    public Color getColor(long binaryPower) {
        return colors.get(binaryPower);
    }

    public Color getFontColor(Color color) {
        return fontColors.get(color);
    }

    public Color getFontColor(int binaryPower) {
        return fontColors.get(getColor(binaryPower));
    }

}
