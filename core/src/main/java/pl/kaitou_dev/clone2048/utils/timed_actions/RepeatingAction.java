package pl.kaitou_dev.clone2048.utils.timed_actions;

/**
 * Extends the concept of an {@link Action} in a way that allows for repeating the same action an exact number of times.
 */
public class RepeatingAction extends Action {
    /**
     * The total number of times for the child {@link Action} to be repeated.
     */
    private final int iterations;
    /**
     * The current iteration number index.
     */
    private int currIteration;
    /**
     * The child {@link Action} to be repeated.
     */
    private final Action action;

    /**
     * The default constructor which takes a number of times to repeat an action, and the {@link Action} to repeat.
     * @param nTimes The number of times to repeat the child {@code Action}.
     * @param action The {@code Action} to repeat.
     */
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

    /**
     * Resets the iteration counter to 0.
     */
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
