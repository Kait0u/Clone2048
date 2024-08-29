package pl.kaitou_dev.clone2048.utils.timed_actions.interpolators;

public interface Interpolator {
    double interpolate(double startVal, double endVal, double timeElapsed, double maxDuration);
}
