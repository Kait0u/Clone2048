package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;

public class BoxAction {
    protected NumberBox box;
    protected boolean isDone;

    public BoxAction(NumberBox box) {
        this.box = box;
        this.isDone = false;
    }

    public void actWithDelta(float delta) {}
    public void act() {
        actWithDelta(Constants.DEFAULT_DELTA);
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public void makeDone() {
        this.isDone = true;
    }
}
