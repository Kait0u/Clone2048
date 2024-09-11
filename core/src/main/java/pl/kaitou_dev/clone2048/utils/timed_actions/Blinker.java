package pl.kaitou_dev.clone2048.utils.timed_actions;

/**
 * A {@code Blinker} simulates a blinking light. It provides access to a time-controlled boolean flag,
 * that is {@code true} or {@code false}, depending on the current moment in time.
 */
public class Blinker extends InfiniteAction {
    /**
     * The default constructor which sets up the {@link BlinkerAction} and turns it into an {@link InfiniteAction}.
     * @param offTimeSeconds The duration for the blinker to be off, measured in seconds.
     * @param onTimeSeconds The duration for the blinker to be on, measured in seconds.
     * @param startOn Whether this {@code Blinker} should start by being on ({@code true}) or off ({@code false}).
     */
    public Blinker(double offTimeSeconds, double onTimeSeconds, boolean startOn) {
        super(new BlinkerAction(offTimeSeconds, onTimeSeconds, startOn));
    }

    @Override
    public void actWithDelta(float delta) {
        super.actWithDelta(delta);
    }

    /**
     * Checks whether this {@code Blinker} is on or off.
     * @return {@code true} if this {@code Blinker} is on, {@code false} if it is off.
     */
    public boolean isOn() {
        BlinkerAction blinkerAction = (BlinkerAction) action;
        return blinkerAction.isOn();
    }

    @Override
    public void reset() {
        super.reset();
        action.reset();
    }
}

/**
 * A specialized {@link Action} that represents a single blinking cycle - on, then off / off, then on.
 */
class BlinkerAction extends Action {
    /**
     * The duration to be off, measured in seconds.
     */
    private final double offTimeSeconds;
    /**
     * The duration to be on, measured in seconds.
     */
    private final double onTimeSeconds;
    /**
     * The time that has elapsed, measured in seconds.
     */
    private double timeElapsed;
    /**
     * Whether this {@code BlinkerAction} should start by being on ({@code true}) or off ({@code false}).
     */
    private final boolean startOn;
    /**
     * Whether this {@code BlinkerAction} is currently on ({@code true}) or off ({@code false}).
     */
    private boolean isCurrentlyOn;

    public BlinkerAction(double offTimeSeconds, double onTimeSeconds, boolean startOn) {
        super();
        this.offTimeSeconds = offTimeSeconds;
        this.onTimeSeconds = onTimeSeconds;

        this.startOn = startOn;
        this.isCurrentlyOn = startOn;
        this.timeElapsed = 0;
    }

    @Override
    public void actWithDelta(float delta) {
        timeElapsed += delta;

        boolean shouldToggle = isCurrentlyOn ? timeElapsed >= onTimeSeconds : timeElapsed >= offTimeSeconds;
        if (shouldToggle) toggle();
    }

    private void toggle() {
        if (startOn) {
            if (isCurrentlyOn) {
                isCurrentlyOn = false;
                resetTimer();
            }
            else makeDone();

        } else {
            if (!isCurrentlyOn) {
                isCurrentlyOn = true;
                resetTimer();
            }
            else makeDone();
        }
    }

    public void resetTimer() {
        timeElapsed = 0;
    }

    @Override
    public void reset() {
        super.reset();
        resetTimer();
        isCurrentlyOn = startOn;
    }

    public boolean isOn() {
        return isCurrentlyOn;
    }
}
