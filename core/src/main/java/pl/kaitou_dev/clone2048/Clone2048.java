package pl.kaitou_dev.clone2048;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Clone2048 extends Game {

    @Override
    public void create() {
        setScreen(new FirstScreen(this));
    }
}
