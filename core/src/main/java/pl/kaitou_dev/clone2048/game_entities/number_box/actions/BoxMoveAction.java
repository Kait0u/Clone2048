package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import com.badlogic.gdx.math.Vector2;
import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;

public class BoxMoveAction extends BoxAction {
    private int destX, destY, startX, startY;
    private float durationSeconds;
    private float elapsedSeconds;

    public BoxMoveAction(NumberBox box, int destX, int destY, float durationSeconds) {
        super(box);
        this.destX = destX;
        this.destY = destY;
        this.durationSeconds = durationSeconds;
        this.elapsedSeconds = 0;

        this.startX = box.getPosX();
        this.startY = box.getPosY();
    }

    public BoxMoveAction(NumberBox box, Vector2 destination, float durationSeconds) {
        this(box, (int) destination.x, (int) destination.y, durationSeconds);
    }

    public BoxMoveAction(NumberBox box) {
        super(box);
    }

    @Override
    public void actWithDelta(float delta) {
        elapsedSeconds += delta;

        if (elapsedSeconds >= durationSeconds) {
            box.setCoords(destX, destY);
            makeDone();
            return;
        }

        float t = elapsedSeconds / durationSeconds;
        int x = (int) (startX + (destX - startX) * t);
        int y = (int) (startY + (destY - startY) * t);
        box.setCoords(x, y);
    }
}
