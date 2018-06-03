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
     * Sample uniformly from {i: 0 &lt; i &lt; length - 1, filter(i) == true}
     * @param length: array size
     * @param filter: predicate which filters any index which returns true
     * @return random index
     * @throws IllegalArgumentException if length is not positive
     * @throws RuntimeException if all elements are filtered
     */
    public static int sample(int length, Predicate<Integer> filter){
        if (length <= 0){
            throw new IllegalArgumentException("Length must be positive.");
        }

        int index = -1;
        int count = 0;

        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            if (filter.test(i)){
                continue;
            }

            count++;

            if (index < 0 || rand.nextDouble() < 1.0 / count){
                index = i;
            }
        }

        if (index < 0){
            throw new RuntimeException("Sampling failed. All elements were filtered by predicate!");
        }

        return index;
    }
}
