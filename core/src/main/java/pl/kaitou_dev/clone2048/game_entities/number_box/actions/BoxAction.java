package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.timed_actions.Action;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

public class BoxAction extends Action {
    protected NumberBox box;

    public BoxAction(NumberBox box) {
        super();

        this.box = box;
    }

    public BoxAction(NumberBox box, Interpolator interpolator) {
        this(box);

        if (interpolator != null)
            this.interpolator = interpolator;

    }

}
