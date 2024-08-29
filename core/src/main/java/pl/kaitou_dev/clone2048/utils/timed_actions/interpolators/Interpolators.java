package pl.kaitou_dev.clone2048.utils.timed_actions.interpolators;

public class Interpolators {
    // Linear interpolation
    public static final Interpolator LINEAR = new Interpolator() {
        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + t * (endVal - startVal);
        }
    };

    // Quadratic interpolation
    public static final Interpolator QUADRATIC = new Interpolator() {

        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + t * t * (endVal - startVal);
        }
    };

    // Cubic interpolation
    public static final Interpolator CUBIC = new Interpolator() {
        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + Math.pow(t, 3) * (endVal - startVal);
        }
    };

    // Bicubic interpolation

    public static final Interpolator BICUBIC = new Interpolator() {
        @Override
        public double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration) {
            if (timeElapsed >= maxDuration) return endVal;

            double t = calcT(timeElapsed, maxDuration);
            return startVal + Math.pow(t, 6) * (endVal - startVal);
        }
    };




    private static double calcT(double timeElapsed, double maxDuration) {
        return timeElapsed / maxDuration;
    }


}
