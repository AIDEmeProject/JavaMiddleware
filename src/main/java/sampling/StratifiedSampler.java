package sampling;

import data.DataPoint;
import user.User;

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
        if (positiveSamples < 0){
            throw new IllegalArgumentException("positiveSamples must be positive: " + positiveSamples);
        }

        if (negativeSamples < 0){
            throw new IllegalArgumentException("negativeSamples must be positive: " + negativeSamples);
        }

        this.positiveSamples = positiveSamples;
        this.negativeSamples = negativeSamples;
    }

    /**
     * Sample from labels array
     * @param labels: array to sample positive and negative samples
     * @return row index of samples
     */
    public int[] sample(int[] labels){
        int[] samples = new int[positiveSamples + negativeSamples];

        if (positiveSamples > 0){
            int[] positiveIndexes = ReservoirSampler.sample(labels.length, positiveSamples, i -> labels[i] != 1);
            System.arraycopy(positiveIndexes, 0, samples, 0, positiveSamples);
        }

        if (negativeSamples > 0){
            int[] negativeIndexes = ReservoirSampler.sample(labels.length, negativeSamples, i -> labels[i] == 1);
            System.arraycopy(negativeIndexes, 0, samples, positiveSamples, negativeSamples);
        }

        return samples;
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
