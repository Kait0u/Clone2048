package pl.kaitou_dev.clone2048.utils.timed_actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the concept of an {@link Action} in a way that allows
 * to perform multiple child {@link Action}s, seemingly at the same time.
 */
public class SimultaneousAction extends Action {
    /**
     * A list of child {@link Action}s.
     */
    private final List<Action> actions;

    /**
     * The default constructor.
     */
    public SimultaneousAction() {
        super();

        actions = new ArrayList<>();
    }

    /**
     * A constructor that allows the user to immediately supply
     * the children {@link Action}s to this {@code SimultaneousAction}.
     * @param actions The child {@link Action}s to be added.
     */
    public SimultaneousAction(Action... actions) {
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
        actions.forEach(action -> {
            action.actWithDelta(delta);
        });

        if (actions.stream().allMatch(Action::isDone)) makeDone();
    }

    @Override
    public void reset() {
        super.reset();

        actions.stream().parallel().forEach(Action::reset);
    }
}
