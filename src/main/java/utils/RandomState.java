package utils;

import java.util.Random;

public class RandomState {
    private final static Random random = new Random();

    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    public static Random newInstance() {
        return new Random(random.nextLong());
    }
}
