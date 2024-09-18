package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

/**
 * A specialized {@link BoxAction} which performs scaling of its {@link NumberBox} on the screen.
 */
public class BoxScaleAction extends BoxAction {
    /**
     * The start value of the scale.
     */
    private double startScale;

    /**
     * The destination value of the scale.
     */
    private final double destScale;

    /**
     * The duration of this {@code BoxScaleAction}, measured in seconds.
     */
    private final float durationSeconds;

    /**
     * Measures how much time has passed since the start of this {@code BoxScaleAction}.
     */
    private float elapsedSeconds;

    /**
     * The default constructor, which takes a reference to the relevant {@link NumberBox}, takes the value of
     * the destination scale, the duration of the scaling and the interpolation method.
     * @param box The {@code NumberBox} this {@code BoxAction} will be applied to.
     * @param destScale The value of the destination scale.
     * @param durationSeconds The duration of this {@code BoxScaleAction}, measured in seconds.
     * @param interpolator The {@link Interpolator} to be used for interpolation.
     */
    public BoxScaleAction(NumberBox box, double destScale, float durationSeconds, Interpolator interpolator) {
        super(box, interpolator);

        this.destScale = destScale;
        this.durationSeconds = durationSeconds;
        elapsedSeconds = 0;

        startScale = box.getScale();
    }

    @Override
    public void actWithDelta(float delta) {
        if (elapsedSeconds == 0) startScale = box.getScale();

        elapsedSeconds += delta;

        if (elapsedSeconds >= durationSeconds) {
            box.setScale(destScale);
            makeDone();
            return;
        }

        double scale = interpolator.interpolate(startScale, destScale, elapsedSeconds, durationSeconds);
        box.setScale(scale);
    }

    @Override
    public void reset() {
        elapsedSeconds = 0;
    }
}
