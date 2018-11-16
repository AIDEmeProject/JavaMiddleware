package explore.sampling;

import data.DataPoint;
import data.IndexedDataset;
import explore.user.User;
import utils.Validator;

import java.util.List;

/**
 * The RandomSampler performs a random selection of initial points of specified size.
 *
 * The underlying algorithm is very simple: if N is the dataset size, and m is the sample size, we repeatedly sample random
 * integers between 0 and N-1 until m distinct values have been retrieved. These m random indexes will be then used to
 * retrieve the data points from the dataset.
 *
 * Note that this algorithm assumes that the sample size is considerably smaller than the underlying dataset; otherwise
 * the performance penalty can be considerably high.
 */
public class RandomSampler implements InitialSampler {
    private final int sampleSize;

    public RandomSampler(int sampleSize) {
        Validator.assertPositive(sampleSize);
        this.sampleSize = sampleSize;
    }

    @Override
    public List<DataPoint> runInitialSample(IndexedDataset unlabeledSet, User user) {
        return unlabeledSet.sample(sampleSize).toList();
    }
}
