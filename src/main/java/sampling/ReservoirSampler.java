package sampling;

import data.DataPoint;

import java.util.*;
import java.util.function.Predicate;

/**
 * Class implementing the Reservoir Sampling algorithm, used for uniformly sampling from an array of unknown size.
 * In our particular case, we want so sample from an array whose elements may be "filtered" by a function
 *
 * @see <a href="https://en.wikipedia.org/wiki/Reservoir_sampling">Wikipedia page</a>
 * @author luciano
 */
public class ReservoirSampler {

    private static final Random random = new Random();

    /**
     * Set the seed for the internal random generator. Used when one wants reproducibility.
     * @param seed: new seed
     */
    public static void setSeed(long seed){
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

    public static <T> Collection<T> sample(Collection<T> collection, int sampleSize, Predicate<T> filter){
        if (sampleSize <= 0){
            throw new IllegalArgumentException("Subset size must be positive: " + sampleSize);
        }

        if (collection.size() < sampleSize){
            throw new IllegalArgumentException("There are less than " + sampleSize + " elements in collection.");
        }

        int index = 0;
        ArrayList<T> result = new ArrayList<>(sampleSize);

        for (T elem : collection) {
            if (filter.test(elem)){
                continue;
            }

            if (index < sampleSize){
                result.add(elem);
            }
            else {
                int j = random.nextInt(index+1);

                if (j < sampleSize) {
                    result.set(j, elem);
                }
            }

            index++;
        }

        return result;
    }

    /**
     * Sample a number of elements uniformly from a collection
     * @param collection: collection to sample from
     * @param sampleSize: random subset size
     * @return sample from collection
     * @throws IllegalArgumentException if length is not positive
     * @throws IllegalArgumentException if there are less than 'subsetSize' elements in collection
     */
    public static <T> Collection<T> sample(Collection<T> collection, int sampleSize){
        return sample(collection, sampleSize, pt -> false);
    }

    /**
     * Sample one single element from a collection
     * @param collection: collection of elements
     * @return random index
     * @throws IllegalArgumentException if length is not positive
     * @throws RuntimeException if collection is empty
     */
    public static <T> T sample(Collection<T> collection){
        return sample(collection, 1).iterator().next();
    }
}
