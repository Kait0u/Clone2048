package pl.kaitou_dev.clone2048.utils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Contains various mathematical and numerical utilities.
 */
public class MathNumUtils {
    /**
     * The source of randomness for random number generation.
     */
    private static final Random random = new Random();

    /**
     * Sets the seed for the random number generator.
     * @param seed The seed for the random number generator.
     */
    public static void setRandomSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * Checks if the provided number is a power of 2.
     * @param n The number to test.
     * @return {@code true} if the number is a power of 2, {@code false} if it is not.
     */
    public static boolean isPowerOfTwo(long n) {
        return (n & (n - 1)) == 0;
    }

    /**
     * Generates a pseudo-random integer from the range [min; max).
     * @param minInclusive The inclusive minimum of the range.
     * @param maxExclusive The exclusive maximum of the range.
     * @return A pseudo-random integer from the range specified by the parameters.
     */
    public static int randInt(int minInclusive, int maxExclusive) {
        return randInt(maxExclusive - minInclusive) + minInclusive;
    }

    /**
     * Generates a pseudo-random non-negative integer from the range [0; max).
     * @param maxExclusive The exclusive maximum of the range.
     * @return A pseudo-random non-negative integer, less than the provided maximum.
     */
    public static int randInt(int maxExclusive) {
        return random.nextInt(maxExclusive);
    }

    /**
     * Chooses one of multiple provided options of the same type.
     * @param options Options to choose from.
     * @return One of the options provided, chosen at random.
     * @param <T> The type of the options provided.
     */
    @SafeVarargs
    public static <T> T randChoice(T... options) {
        if (options == null || options.length == 0) throw new IllegalArgumentException("Illegal options!");
        return options[MathNumUtils.randInt(options.length)];
    }

    /**
     * Simulates a dice throw and checks if the number thus achieved is equal to the tested number.
     * @param sides The number of sides on the dice.
     *              Each side has an equal probability of appearing.
     * @param testVal The value to test for equality with.
     * @return {@code true} if the number from the dice equals the provided number,
     *         {@code false} if it does not.
     */
    public static boolean diceTest(int sides, int testVal) {
        return testVal == randInt(1, sides + 1);
    }

    /**
     * Creates an {@link IntStream} that provides elements in a reverse order.
     * @param source The source {@code IntStream}.
     * @return An {@code IntStream} that provides the same elements as the {@code source}, but in a reverse order.
     */
    public static IntStream reverseIntStream(IntStream source) {
        List<Integer> temp = new ArrayList<>(source.boxed().toList());
        Collections.reverse(temp);
        return temp.stream().mapToInt(Integer::intValue);
    }
}
