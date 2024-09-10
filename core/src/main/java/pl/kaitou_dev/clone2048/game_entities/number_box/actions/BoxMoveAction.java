package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

/**
 * A specialized {@link BoxAction} which performs a movement of its {@link NumberBox} on the screen.
 */
public class BoxMoveAction extends BoxAction {
    /**
     * The X coordinate of the starting point.
     */
    private final int startX;
    /**
     * The Y coordinate of the starting point.
     */
    private final int startY;

    /**
     * The X coordinate of the destination point.
     */
    private final int destX;
    /**
     * The Y coordinate of the destination point.
     */
    private final int destY;

    /**
     * The duration of this {@code BoxMoveAction}, measured in seconds.
     */
    private final float durationSeconds;
    /**
     * Measures how much time has passed since the start of this {@code BoxMoveAction}.
     */
    private float elapsedSeconds;

    /**
     * The default constructor, which takes a reference to the relevant {@link NumberBox}, takes the coordinates of
     * the destination point, the duration of the movement and the interpolation method.
     * @param box The {@code NumberBox} this {@code BoxAction} will be applied to.
     * @param destX The X coordinate of the destination point.
     * @param destY The Y coordinate of the destination point.
     * @param durationSeconds The duration of this {@code BoxMoveAction}, measured in seconds.
     * @param interpolator The {@link Interpolator} to be used for interpolation.
     */
    public BoxMoveAction(NumberBox box, int destX, int destY, float durationSeconds, Interpolator interpolator) {
        super(box, interpolator);

        this.destX = destX;
        this.destY = destY;
        this.durationSeconds = durationSeconds;
        this.elapsedSeconds = 0;

        this.startX = box.getPosX();
        this.startY = box.getPosY();
    }

    @Override
    public void actWithDelta(float delta) {
        elapsedSeconds += delta;

        if (elapsedSeconds >= durationSeconds) {
            box.setCoords(destX, destY);
            makeDone();
            return;
        }

        int x = (int) interpolator.interpolate(startX, destX, elapsedSeconds, durationSeconds);
        int y = (int) interpolator.interpolate(startY, destY, elapsedSeconds, durationSeconds);
        box.setCoords(x, y);
    }

    @Override
    public void reset() {
        elapsedSeconds = 0;
    }
}
