package pl.kaitou_dev.clone2048.utils.timed_actions;

import pl.kaitou_dev.clone2048.Constants;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolators;

/**
 * Represents an action that takes a certain amount of time to finish,
 * the progress on which depends on the delta-time and the interpolation method used.
 */
public abstract class Action {
    /**
     * A boolean flag informing whether this {@code Action} has been completed ({@code true}) or not ({@code false}).
     */
    protected boolean isDone;
    /**
     * The {@link Interpolator} used in evaluating this {@code Action}'s progress.
     * The default {@code Interpolator} is {@link Interpolators#LINEAR}.
     */
    protected Interpolator interpolator = Interpolators.LINEAR;

    /**
     * The default constructor, which only sets the {@link Action#isDone} flag to {@code false}.
     */
    public Action() {
        this.isDone = false;
    }

    /**
     * An extended constructor, which allows to specify the interpolation method.
     * @param interpolator The {@link Interpolator} to be used for interpolation.
     */
    public Action(Interpolator interpolator) {
        this();
        this.interpolator = interpolator;
    }

    /**
     * Updates the progress on this {@code Action}, using the provided delta-time.
     * @param delta The delta-time with which to establish the current progress.
     */
    public abstract void actWithDelta(float delta);

    /**
     * A simplified version of {@link Action#actWithDelta}, which assumes a default delta-time value.
     * @see Constants#DEFAULT_DELTA
     */
    public void act() {
        actWithDelta(Constants.DEFAULT_DELTA);
    }

    /**
     * Resets the progress on this {@code Action}, allowing for it to be restarted.
     */
    public void reset() {
        isDone = false;
    }

    /**
     * Checks if this {@code Action} has already been completed.
     * @return {@code true} if this {@code Action} has been completed, {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Sets the {@link Action#isDone} flag to the specified value.
     * @param done The new value for the {@code isDone} flag.
     */
    public void setDone(boolean done) {
        this.isDone = done;
    }

    /**
     * Sets the {@code isDone} flag to {@code true}, indicating that this {@code Action} will no longer update,
     * unless reset.
     */
    public void makeDone() {
        this.isDone = true;
    }
}

