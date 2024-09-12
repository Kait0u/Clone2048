package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import pl.kaitou_dev.clone2048.game_entities.Directions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains various controls-related utility methods.
 */
public class ControlUtils {
    /**
     * A map that maps Gdx-encoded direction keys onto {@link Directions}.
     */
    private static final Map<Integer, Directions> keysDirectionsMap;

    static {
        keysDirectionsMap = new HashMap<>() {{
            // Up
            put(Input.Keys.UP, Directions.UP);
            put(Input.Keys.W, Directions.UP);

            // Down
            put(Input.Keys.DOWN, Directions.DOWN);
            put(Input.Keys.S, Directions.DOWN);

            // Left
            put(Input.Keys.LEFT, Directions.LEFT);
            put(Input.Keys.A, Directions.LEFT);

            // Right
            put(Input.Keys.RIGHT, Directions.RIGHT);
            put(Input.Keys.D, Directions.RIGHT);
        }};
    }

    /**
     * Gets a {@code Set} of Gdx-encoded keys.
     * @return A {@code Set} of Gdx-encoded keys.
     */
    public static Set<Integer> getDirectionKeys() {
        return keysDirectionsMap.keySet();
    }

    /**
     * Gets a corresponding of the {@link Directions}, based on the Gdx key code provided.
     * @param gdxKey Gdx-encoded key.
     * @return One of the {@code Directions} corresponding to the key,
     *         or {@code null} if the code was not a directional key, or was invalid.
     */
    public static Directions getDirection(int gdxKey) {
        return keysDirectionsMap.getOrDefault(gdxKey, null);
    }

    /**
     * Checks if any key has been <b>just</b> pressed.
     * @return {@code true} if any key has just been pressed, {@code false} otherwise.
     */
    public boolean isAnyKeyJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY);
    }
}
