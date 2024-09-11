package pl.kaitou_dev.clone2048.utils.timed_actions;

/**
 * Extends the concept of an {@link Action} in a way that allows for constantly repeating the same action
 * over and over again.
 */
public class InfiniteAction extends Action {
    /**
     * A boolean flag indicating whether this {@code InfiniteAction} is running ({@code true}) or not ({@code false}).
     */
    private boolean isRunning;

    /**
     * The action to repeat infinitely.
     */
    protected final Action action;

    /**
     * The default constructor, which accepts an {@link Action} to repeat. This {@code InfiniteAction}
     * needs to be started manually, using the {@link InfiniteAction#start()} method,
     * otherwise all calls to {@link Action#actWithDelta(float)} will be skipped.
     *
     * @param action The action to repeat infinitely.
     */
    public InfiniteAction(Action action) {
        super();

        this.action = action;
        this.isRunning = false;
    }

    @Override
    public void actWithDelta(float delta) {
        if (action == null || !isRunning) return;

        if (action.isDone()) action.reset();
        action.actWithDelta(delta);
    }

    /**
     * Starts this {@code InfiniteAction} - it will accept updates from now on.
     */
    public void start() {
        isRunning = true;
    }

    /**
     * Stops this {@code InfiniteAction} - it will ignore updates from now on.
     */
    public void stop() {
        isRunning = false;
    }

    @Override
    public void reset() {
        super.reset();
        action.reset();
    }

    /**
     * Gets the {@link Action} that is being handled by this {@code InfiniteAction}.
     * @return The {@code Action} currently handled by this {@code InfiniteAction}.
     */
    public Action getAction() {
        return action;
    }
}
