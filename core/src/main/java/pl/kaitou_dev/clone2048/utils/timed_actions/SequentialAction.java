package pl.kaitou_dev.clone2048.utils.timed_actions;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Extends the concept of an {@link Action} in a way that allows
 * to perform multiple child {@link Action}s, one after another.
 */
public class SequentialAction extends Action {
    /**
     * A queue of {@link Action}s that are yet to be finished.
     */
    private final Queue<Action> actions;
    /**
     * A queue of finished {@link Action}s.
     */
    private final Queue<Action> actionsDone;
    /**
     * The currently performed {@link Action}.
     */
    private Action currentAction = null;

    /**
     * The default constructor.
     */
    public SequentialAction() {
        super();

        actions = new ArrayDeque<>();
        actionsDone = new ArrayDeque<>();
    }

    /**
     * A constructor that allows the user to immediately supply
     * the children {@link Action}s to this {@code SequentialAction}.
     * @param actions The child {@link Action}s to be added.
     */
    public SequentialAction(Action... actions) {
        this();

        addActions(actions);
    }

    /**
     * Adds a new child {@link Action}.
     * @param action The child {@code Action} to be added.
     */
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Adds multiple new child {@link Action}s.
     * @param actions The child {@code Action}s to be added.
     */
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
