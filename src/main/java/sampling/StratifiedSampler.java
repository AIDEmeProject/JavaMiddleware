package sampling;

import data.DataPoint;
import user.User;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

public class StratifiedSampler {
    /**
     * number of positive samples to be sampled
     */
    private final int positiveSamples;

    /**
     * number of negative samples to be sampled
     */
    private final int negativeSamples;

    public StratifiedSampler(int positiveSamples, int negativeSamples) {
        Validator.assertPositive(positiveSamples);
        Validator.assertPositive(negativeSamples);

        this.positiveSamples = positiveSamples;
        this.negativeSamples = negativeSamples;
    }

    public Collection<DataPoint> sample(Collection<DataPoint> points, User user){
        Collection<DataPoint> samples = new ArrayList<>(positiveSamples + negativeSamples);

        if (positiveSamples > 0){
            Collection<DataPoint> positive = ReservoirSampler.sample(points, positiveSamples, pt -> user.getLabel(pt) != 1);
            samples.addAll(positive);
        }

        if (negativeSamples > 0){
            Collection<DataPoint> negative = ReservoirSampler.sample(points, positiveSamples, pt -> user.getLabel(pt) == 1);
            samples.addAll(negative);
        }

        return samples;
    }
}
