package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Contains various font-related utilities,
 * as well as methods that produce {@link BitmapFont}s for the TTF fonts provided with the game.
 */
public class FontUtils {
    /**
     * Gets a font from the "fonts" directory, given by its filename,
     * turning it into a {@link BitmapFont} of provided size.
     * @param filename The filename of the font file.
     *                 WARNING: it is NOT a path, it is the name of the file in the assets/fonts directory
     * @param size The size value for the {@code BitmapFont} to be produced.
     * @return The TTF font as a {@code BitmapFont}
     */
    private static BitmapFont getFont(String filename, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + filename));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return font;

    }

    /**
     * Gets the "losevka" font.
     * @param size The size of the produced {@link BitmapFont}.
     * @return The "losevka" font as a {@link BitmapFont}
     */
    public static BitmapFont losevka(int size) {
        return getFont("losevka.ttf", size);
    }

    /**
     * Gets the "monofett" font.
     * @param size The size of the produced {@link BitmapFont}.
     * @return The "monofett" font as a {@link BitmapFont}
     */
    public static BitmapFont monofett(int size) {
        return getFont("Monofett-Regular.ttf", size);
    }
}
