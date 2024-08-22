package pl.kaitou_dev.clone2048;

import com.badlogic.gdx.graphics.g2d.Batch;
import pl.kaitou_dev.clone2048.utils.MathUtils;

public class NumberBox {
    private int value;



    public NumberBox(int value) throws IllegalArgumentException {
        if (!MathUtils.isPowerOfTwo(value)) throw new IllegalArgumentException("Value must be a power of two");

        this.value = value;
    }

    public void draw(Batch batch) {

    }

    public String toString() {
        return String.format("[%4s]", value);
    }
}
