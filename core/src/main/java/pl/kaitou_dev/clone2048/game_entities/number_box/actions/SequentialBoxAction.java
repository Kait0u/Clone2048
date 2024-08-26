package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;

import java.util.ArrayDeque;
import java.util.Queue;

public class SequentialBoxAction extends BoxAction {
    private Queue<BoxAction> actions;
    private BoxAction currentAction = null;

    public SequentialBoxAction(NumberBox box) {
        super(box);

        actions = new ArrayDeque<>();
    }

    public SequentialBoxAction(NumberBox box, BoxAction... actions) {
        this(box);

        addActions(actions);
    }

    public void addAction(BoxAction action) {
        actions.add(action);
    }

    public void addActions(BoxAction... actions) {
        for (BoxAction action : actions) {
            addAction(action);
        }
    }

    @Override
    public void actWithDelta(float delta) {
        if (currentAction == null)
            currentAction = actions.poll();

        if (currentAction != null) {
            currentAction.actWithDelta(delta);

            if (currentAction.isDone()) currentAction = null;
        } else {
            makeDone();
        }
    }
}
