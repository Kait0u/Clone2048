package pl.kaitou_dev.clone2048.utils.timed_actions;

public class InfiniteAction<A extends Action> extends Action {
    private boolean isRunning;
    protected final Action action;

    public InfiniteAction(A action) {
        super();

        this.action = action;
    }

    @Override
    public void actWithDelta(float delta) {
        if (action == null || !isRunning) return;

        if (action.isDone()) action.reset();
        action.actWithDelta(delta);
    }

    public void start() {
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void reset() {
        super.reset();
        action.reset();
    }

    public A getAction() {
        return (A) action;
    }


}
