package pl.kaitou_dev.clone2048.game_entities.number_box.actions;

import pl.kaitou_dev.clone2048.game_entities.number_box.NumberBox;
import pl.kaitou_dev.clone2048.utils.timed_actions.Action;
import pl.kaitou_dev.clone2048.utils.timed_actions.interpolators.Interpolator;

/**
 * Represents a specialized {@link Action} that deals with {@link NumberBox}es.
 */
public abstract class BoxAction extends Action {
    /**
     * A reference to the {@link NumberBox} this {@code BoxAction} should apply to.
     */
    protected final NumberBox box;

    /**
     * The default constructor, establishing a reference to some {@link NumberBox}.
     * @param box The {@code NumberBox} this {@code BoxAction} will be applied to.
     */
    public BoxAction(NumberBox box) {
        super();
        this.box = box;
    }

    /**
     * The extended constructor, which takes a reference to the {@link NumberBox} this {@code BoxAction} will be
     * applied to, as well as the preferred method of interpolation.
     * @param box The {@code NumberBox} this {@code BoxAction} will be applied to.
     * @param interpolator The {@link Interpolator} to be used for interpolation.
     */
    public BoxAction(NumberBox box, Interpolator interpolator) {
        this(box);

        if (interpolator != null)
            this.interpolator = interpolator;

    }

}
