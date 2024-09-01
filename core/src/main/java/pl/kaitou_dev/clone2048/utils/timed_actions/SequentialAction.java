package pl.kaitou_dev.clone2048.utils.timed_actions;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class SequentialAction extends Action {
    private final Queue<Action> actions;
    private final Queue<Action> actionsDone;
    private Action currentAction = null;


    public SequentialAction() {
        super();

        actions = new ArrayDeque<>();
        actionsDone = new ArrayDeque<>();
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

            if (currentAction.isDone()) {
                actionsDone.add(currentAction);
                currentAction = null;
            }
        } else {
            makeDone();
        }
    }

    @Override
    public void reset() {
        super.reset();

        List<Action> stillUndone = actions.stream().toList();
        actions.clear();

        actionsDone.stream().parallel().forEach(Action::reset);
        actions.addAll(actionsDone);

        if (currentAction != null) {
            currentAction.reset();
            actions.add(currentAction);
        }

        currentAction = null;

        actions.addAll(stillUndone);

    }
}
