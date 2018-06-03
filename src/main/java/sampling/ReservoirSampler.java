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
    /**
     * Sample k elements uniformly from {i: 0 &lt; i &lt; length - 1, filter(i) == true}
     * @param length: array size
     * @param k: random subset size
     * @param filter: predicate which filters any index which returns true
     * @return random index
     * @throws IllegalArgumentException if length is not positive
     * @throws RuntimeException if there are less than k non-filtered elements
     */
    public static int[] sample(int length, int k, Predicate<Integer> filter){
        if (length <= 0){
            throw new IllegalArgumentException("Length must be positive: " + length);
        }

        if (k <= 0){
            throw new IllegalArgumentException("k out of bounds: " + k);
        }

        int index = 0;
        int[] result = new int[k];
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            if (filter.test(i)){
                continue;
            }

            if (index < k){
                result[index++] = i;
            }
            else {
                int j = rand.nextInt(i+1);

                if (j < k) {
                    result[j] = i;
                }
            }
        }

        if (index < k){
            throw new RuntimeException("Sampling failed. There are less than " + k + " non-filtered elements in array.");
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
