package sampling;

import java.util.ArrayList;
import java.util.Collection;
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

    private static final Random random = new Random();

    /**
     * Set the seed for the internal random generator. Used when one wants reproducibility.
     * @param seed: new seed
     */
    public static void setSeed(long seed){
        random.setSeed(seed);
    }

    /**
     * Extracts a random sample from a collection. We can also specify a filter function for
     * @param collection: collection of elements
     * @param sampleSize: sample size
     * @param filter: predicate which filters any element returning true
     * @return random index
     * @throws IllegalArgumentException if length is not positive
     * @throws IllegalArgumentException if sample size is larger than filtered collection
     */
    public static <T> Collection<T> sample(Collection<T> collection, int sampleSize, Predicate<T> filter){
        if (sampleSize <= 0){
            throw new IllegalArgumentException("Subset size must be positive: " + sampleSize);
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

        if (index < sampleSize){
            throw new IllegalArgumentException("There are less than " + sampleSize + " elements in collection after filtering.");
        }

        return result;
    }

    /**
     * Extracts a random sample from a collection.
     * @param collection: collection to sample from
     * @param sampleSize: random subset size
     * @return sample from collection
     * @throws IllegalArgumentException if length is not positive
     * @throws IllegalArgumentException sample size is larger than collection
     */
    public static <T> Collection<T> sample(Collection<T> collection, int sampleSize){
        return sample(collection, sampleSize, pt -> false);
    }

    /**
     * Sample one single element from a collection
     * @param collection: collection of elements
     * @return random index
     * @throws IllegalArgumentException if collection is empty
     */
    public static <T> T sample(Collection<T> collection){
        return sample(collection, 1).iterator().next();
    }
}
