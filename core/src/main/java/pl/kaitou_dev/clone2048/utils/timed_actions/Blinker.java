package pl.kaitou_dev.clone2048.utils.timed_actions;

public class Blinker extends InfiniteAction<Action> {
    public Blinker(double offTimeSeconds, double onTimeSeconds, boolean startOn) {
        super(new BlinkerAction(offTimeSeconds, onTimeSeconds, startOn));
    }

    @Override
    public void actWithDelta(float delta) {
        super.actWithDelta(delta);
    }

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

class BlinkerAction extends Action {
    private final double offTimeSeconds;
    private final double onTimeSeconds;
    private double timeElapsed;
    private final boolean startOn;
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
