package pl.kaitou_dev.clone2048.utils.timed_actions;

import java.util.ArrayDeque;
import java.util.Queue;

public class SequentialAction extends Action {
    private final Queue<Action> actions;
    private Action currentAction = null;

    public SequentialAction() {
        super();

        actions = new ArrayDeque<>();
    }

    public SequentialAction(Action... actions) {
        this();

        addActions(actions);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void addActions(Action... actions) {
        for (Action action : actions) {
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
