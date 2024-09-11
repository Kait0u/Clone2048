package pl.kaitou_dev.clone2048.utils.timed_actions.interpolators;

/**
 * Provides several common implementations of the {@link Interpolator}
 * interface, allowing for different types of interpolation (linear, quadratic, cubic, bicubic).
 */
public class Interpolators {
    // Linear interpolation
    /**
     * Interpolates linearly between two values, meaning the rate of change is constant over time.
     * The result follows the equation:
     * {@code result = startVal + t * (endVal - startVal)} where {@code t = timeElapsed / maxDuration}.
     */
    public static final Interpolator LINEAR = new Interpolator() {
        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + t * (endVal - startVal);
        }
    };

    // Quadratic interpolation
    /**
     * Interpolates using a quadratic curve, where the rate of change follows a squared function.
     * The result follows the equation:
     * {@code result = startVal + t^2 * (endVal - startVal)} where {@code t = timeElapsed / maxDuration}.
     */
    public static final Interpolator QUADRATIC = new Interpolator() {

        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + t * t * (endVal - startVal);
        }
    };

    // Cubic interpolation
    /**
     * Interpolates using a cubic curve, where the rate of change follows a cubic function.
     * The result follows the equation:
     * {@code result = startVal + t^3 * (endVal - startVal)} where {@code t = timeElapsed / maxDuration}.
     */
    public static final Interpolator CUBIC = new Interpolator() {
        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + Math.pow(t, 3) * (endVal - startVal);
        }
    };

    // Bicubic interpolation
    /**
     * Interpolates using a bicubic curve, where the rate of change follows a bicubic function.
     * The result follows the equation:
     * {@code result = startVal + t^6 * (endVal - startVal)} where {@code t = timeElapsed / maxDuration}.
     */
    public static final Interpolator BICUBIC = new Interpolator() {
        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + Math.pow(t, 6) * (endVal - startVal);
        }
    };

    /**
     * Helper method to calculate the normalized time ratio (t) based on the elapsed time and
     * the maximum duration.
     *
     * @param timeElapsed  The amount of time that has passed since interpolation began.
     * @param maxDuration  The total duration for the interpolation process.
     * @return A value between 0 and 1 representing the proportion of time elapsed.
     */
    private static double calcT(double timeElapsed, double maxDuration) {
        return timeElapsed / maxDuration;
    }


}
