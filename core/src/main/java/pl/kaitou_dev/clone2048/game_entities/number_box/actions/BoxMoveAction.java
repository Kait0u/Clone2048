package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

public class BoxMoveAction extends BoxAction {
    private int destX, destY, startX, startY;
    private float durationSeconds;
    private float elapsedSeconds;

    public BoxMoveAction(NumberBox box, int destX, int destY, float durationSeconds, Interpolator interpolator) {
        super(box, interpolator);

        this.destX = destX;
        this.destY = destY;
        this.durationSeconds = durationSeconds;
        this.elapsedSeconds = 0;

        this.startX = box.getPosX();
        this.startY = box.getPosY();
    }

    public BoxMoveAction(NumberBox box, int destX, int destY, float durationSeconds) {
        this(box, destX, destY, durationSeconds, null);
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
