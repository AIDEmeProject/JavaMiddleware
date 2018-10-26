package utils;

import java.util.Random;

/**
 * This class is a factory of Random objects. We create Random instances in a "controlled" fashion, which allows us to
 * make experiments reproducible. All classes should avoid creating Random instances by themselves, and instead call
 * newInstance() method below.
 *
 * Although having such a global state class is generally a bad practice, it is the choice that involves the least amount
 * of changes to the current codebase.
 */
public final class RandomState {
    /**
     * Random seed generator
     */
    private final static Random random = new Random();

    /**
     * This method should only be called once per phase (exploration, evaluation)
     * @param seed: new seed for the RNG
     */
    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * @return a new Random instance. Its seed comes from the internal RNG.
     */
    public static Random newInstance() {
        return new Random(random.nextLong());
    }
}
