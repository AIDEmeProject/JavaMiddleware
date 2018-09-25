package explore.sampling;

import data.DataPoint;
import explore.user.User;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public List<DataPoint> runInitialSample(Collection<DataPoint> unlabeledSet, User user){
        List<DataPoint> samples = new ArrayList<>(positiveSamples + negativeSamples);

        if (positiveSamples > 0){
            Collection<DataPoint> positive = ReservoirSampler.sample(unlabeledSet, positiveSamples, pt -> user.getLabel(pt).isNegative());
            samples.addAll(positive);
        }

        if (negativeSamples > 0){
            Collection<DataPoint> negative = ReservoirSampler.sample(unlabeledSet, negativeSamples, pt -> user.getLabel(pt).isPositive());
            samples.addAll(negative);
        }

        return samples;
    }
}
