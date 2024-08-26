package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;

import java.util.ArrayList;
import java.util.List;

public class SimultaneousBoxAction extends BoxAction {
    private List<BoxAction> actions;

    public SimultaneousBoxAction(NumberBox box) {
        super(box);

        actions = new ArrayList<BoxAction>();
    }

    public SimultaneousBoxAction(NumberBox box, BoxAction... actions) {
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
        actions.forEach(action -> {
            if (!action.isDone())
                action.actWithDelta(delta);
        });
    }
}
