package pl.kaitou_dev.clone2048.utils.timed_actions;

import java.util.ArrayList;
import java.util.List;

public class SimultaneousAction extends Action {
    private final List<Action> actions;

    public SimultaneousAction() {
        super();

        actions = new ArrayList<>();
    }

    public SimultaneousAction(Action... actions) {
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
        actions.forEach(action -> {
            if (!action.isDone())
                action.actWithDelta(delta);
        });
    }
}
