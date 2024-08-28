package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;


public class FontUtils {
    private static BitmapFont getFont(String filename, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return font;

    }

    public static BitmapFont losevka(int size) {
        return getFont("fonts/losevka.ttf", size);
    }

    public static BitmapFont monofett(int size) {
        return getFont("fonts/Monofett-Regular.ttf", size);
    }
}
