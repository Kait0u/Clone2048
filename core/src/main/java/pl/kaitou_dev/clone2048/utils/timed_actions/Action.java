package pl.kaitou_dev.clone2048.utils.timed_actions;

import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolators;

public class Action {
    protected boolean isDone;
    protected Interpolator interpolator = Interpolators.LINEAR;

    public Action() {
        this.isDone = false;
    }

    public Action(Interpolator interpolator) {
        this();
        this.interpolator = interpolator;
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

