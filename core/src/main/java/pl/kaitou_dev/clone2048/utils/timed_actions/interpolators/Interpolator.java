package pl.kaitou_dev.clone2048.utils.timed_actions.interpolators;

/**
 * The Interpolator interface defines a method for interpolating between two values
 * based on the time elapsed and a given maximum duration.
 */
public interface Interpolator {
    /**
     * Interpolates between a starting value and an ending value based on the
     * elapsed time and the maximum allowed duration.
     *
     * @param startVal      The initial value at the start of interpolation.
     * @param endVal        The target value at the end of interpolation.
     * @param timeElapsed   The amount of time that has passed since interpolation began.
     *                      This value should be between 0 and {@code maxDuration}.
     * @param maxDuration   The total time duration for the interpolation process.
     *                      When {@code timeElapsed} equals {@code maxDuration},
     *                      the method should return {@code endVal}.
     * @return              A double representing the interpolated value at the given time.
     */
    double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration);
}
