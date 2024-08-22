package pl.kaitou_dev.clone2048.utils;

public class MathUtils {
    public static boolean isPowerOfTwo(int n) {
        return (n & (n - 1)) == 0;
    }
}
