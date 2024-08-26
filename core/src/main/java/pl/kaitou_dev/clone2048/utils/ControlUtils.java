package pl.kaitou_dev.clone2048.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import pl.kaitou_dev.clone2048.game_entities.Directions;

import java.util.HashMap;
import java.util.Map;

public class ControlUtils {
    private static Map<Integer, Directions> keysDirectionsMap;

    static {
        keysDirectionsMap = new HashMap<>() {{
            put(Input.Keys.UP, Directions.UP);
            put(Input.Keys.DOWN, Directions.DOWN);
            put(Input.Keys.LEFT, Directions.LEFT);
            put(Input.Keys.RIGHT, Directions.RIGHT);
        }};
    }


}
