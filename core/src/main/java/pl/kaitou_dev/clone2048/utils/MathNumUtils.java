package pl.kaitou_dev.clone2048.utils;

import java.util.Random;

public class MathNumUtils {
    private static Random random = new Random();

    public static boolean isPowerOfTwo(int n) {
        return (n & (n - 1)) == 0;
    }

    public static void setRandomSeed(long seed) {
        random.setSeed(seed);
    }

    public static int randInt(int minInclusive, int maxExclusive) {
        return randInt(maxExclusive - minInclusive) + minInclusive;
    }

    public static int randInt(int maxExclusive) {
        return random.nextInt(maxExclusive);
    }

    public static boolean diceTest(int sides, int testVal) {
        return testVal == randInt(1, sides + 1);
    }
}
