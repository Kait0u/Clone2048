package pl.kaitou_dev.clone2048.game_entities.number_box;

import pl.kaitou_dev.clone2048.utils.MathNumUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds and serves items associated with consecutive binary powers.
 * @param <T>
 */
public abstract class BinaryPalette<T> {
    /**
     * A counter that will become the next power of 2, as more items are added to a palette.
     * Its initial value is 2, which ensures that the first item corresponds to the number 2.
     */
    protected long nextBinaryPower = 2;

    /**
     * A map that maps binary powers onto items offered by this {@code BinaryPalette}.
     */
    protected final Map<Long, T> items;

    /**
     * The default constructor, which sets up the map.
     */
    protected BinaryPalette() {
        items = new HashMap<Long, T>();
    }

    /**
     * Increases the next binary power.
     * @see BinaryPalette#nextBinaryPower
     */
    protected void increasePower() {
        nextBinaryPower <<= 1;
    }

    /**
     * Resets the binary power to its initial value of 2.
     */
    protected void resetPower() {
        nextBinaryPower = 2;
    }

    /**
     * Clears the palette, and resets its counter.
     */
    protected void clear() {
        items.clear();
        resetPower();
    }

    /**
     * Adds an item to be offered at the next available binary power.
     * @param item The item to be added.
     */
    public void addItem(T item) {
        items.put(nextBinaryPower, item);
        increasePower();
    }

    /**
     * Gets the item stored at the provided binary power.
     * @param binaryPower The binary power whose associated item to get.
     * @return The item associated with the binary power,
     *         or {@code null} if the binary power has nothing associated with it.
     * @throws IllegalArgumentException if the provided number is not a power of 2.
     */
    public T getItem(long binaryPower) {
        if (!MathNumUtils.isPowerOfTwo(binaryPower))
            throw new IllegalArgumentException("The provided number must be a power of two");

        return items.get(binaryPower);
    }
}
