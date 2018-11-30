package explore.sampling;

import data.DataPoint;
import data.IndexedDataset;
import explore.user.User;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StratifiedSampler implements InitialSampler{
    /**
     * number of positive samples to be sampled
     */
    private final int positiveSamples;

    /**
     * number of negative samples to be sampled
     */
    private final int negativeSamples;

    /**
     * @throws IllegalArgumentException if either positiveSamples or negativeSamples are negative
     */
    public StratifiedSampler(int positiveSamples, int negativeSamples) {
        Validator.assertPositive(positiveSamples);
        Validator.assertPositive(negativeSamples);

        this.positiveSamples = positiveSamples;
        this.negativeSamples = negativeSamples;
    }

    /**
     * @param unlabeledSet: initial collection of unlabeled points
     * @param user: user instance for labeling points
     * @return a list of DataPoints containing the specified number of positive and negative samples
     */
    public List<DataPoint> runInitialSample(IndexedDataset unlabeledSet, User user){
        List<DataPoint> samples = new ArrayList<>(positiveSamples + negativeSamples);

        if (positiveSamples > 0){
            List<DataPoint> positivePoints = unlabeledSet.stream()
                    .filter(x -> user.getLabel(x).isPositive())
                    .collect(Collectors.toList());

            if (positivePoints.size() < positiveSamples) {
                throw new RuntimeException("Dataset does not contain " + positivePoints + " positive points.") ;
            }

            samples.addAll(ReservoirSampler.sample(positivePoints, positiveSamples));
        }

        if (negativeSamples > 0){
            List<DataPoint> negativePoints = unlabeledSet.stream()
                    .filter(x -> user.getLabel(x).isNegative())
                    .collect(Collectors.toList());

            if (negativePoints.size() < negativeSamples) {
                throw new RuntimeException("Dataset does not contain " + negativePoints + " negative points.") ;
            }

            samples.addAll(ReservoirSampler.sample(negativePoints, negativeSamples));
        }

        return samples;
    }

    @Override
    public String toString() {
        return "StratifiedSampler{" +
                "positiveSamples=" + positiveSamples +
                ", negativeSamples=" + negativeSamples +
                '}';
    }
}
