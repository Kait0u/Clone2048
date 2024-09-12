package pl.kaitou_dev.clone2048.utils.timed_actions;

public class RepeatingAction extends Action {
    private final int iterations;
    private int currIteration;
    private final Action action;

    public RepeatingAction(int nTimes, Action action) {
        super();

        this.iterations = nTimes;
        this.action = action;

        currIteration = 0;
    }

    @Override
    public void actWithDelta(float delta) {
        if (action == null) return;

        if (currIteration < iterations) {
            if (!action.isDone()) action.actWithDelta(delta);
            else {
                ++currIteration;
                action.reset();
            }
        } else makeDone();
    }

    public void resetCounter() {
        currIteration = 0;
    }

    @Override
    public void reset() {
        super.reset();

        resetCounter();
        action.reset();
    }
}
