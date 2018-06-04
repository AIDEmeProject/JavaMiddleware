package sampling;

import java.util.Random;
import java.util.function.Predicate;

/**
 * Class implementing the Reservoir Sampling algorithm, used for uniformly sampling from an array of unknown size.
 * In our particular case, we want so sample from an array whose elements may be "filtered" by a function
 *
 * @see <a href="https://en.wikipedia.org/wiki/Reservoir_sampling">Wikipedia page</a>
 * @author luciano
 */
public class ReservoirSampler {

    private static Random random = new Random();

    /**
     * Set the seed for the internal random generator. Used when one wants reproducibility.
     * @param seed: new seed
     */
    public static void setSeed(int seed){
        random.setSeed(seed);
    }

    /**
     * Sample k elements uniformly from {i: 0 &lt; i &lt; length - 1, filter(i) == false}
     * @param length: array size
     * @param subsetSize: random subset size
     * @param filter: predicate which filters any index returning true
     * @return random index
     * @throws IllegalArgumentException if length is not positive
     * @throws RuntimeException if there are less than k non-filtered elements
     */
    public static int[] sample(int length, int subsetSize, Predicate<Integer> filter){
        if (length <= 0){
            throw new IllegalArgumentException("Length must be positive: " + length);
        }

        if (subsetSize <= 0){
            throw new IllegalArgumentException("Subset size must be positive: " + subsetSize);
        }

        int index = 0;
        int[] result = new int[subsetSize];

        for (int i = 0; i < length; i++) {
            if (filter.test(i)){
                continue;
            }

            if (index < subsetSize){
                result[index] = i;
            }
            else {
                int j = random.nextInt(index+1);

                if (j < subsetSize) {
                    result[j] = i;
                }
            }

            index++;
        }

        if (index < subsetSize){
            throw new RuntimeException("Sampling failed. There are less than " + subsetSize + " non-filtered elements in array.");
        }

        return result;
    }

    /**
     * Sample one single element from {i: 0 &lt; i &lt; length - 1, filter(i) == true}
     * @param length: array size
     * @param filter: predicate which filters any index which returns true
     * @return random index
     * @throws IllegalArgumentException if length is not positive
     * @throws RuntimeException if all elements are filtered
     */
    public static int sample(int length, Predicate<Integer> filter){
        return sample(length, 1, filter)[0];
    }
}
