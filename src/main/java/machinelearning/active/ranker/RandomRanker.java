package machinelearning.active.ranker;

import data.DataPoint;
import explore.sampling.ReservoirSampler;
import machinelearning.active.Ranker;

import java.util.Collection;

public class RandomRanker implements Ranker {

    /**
     * @return a random a point from the input collection
     */
    @Override
    public DataPoint top(Collection<DataPoint> unlabeledSet) {
        return ReservoirSampler.sample(unlabeledSet);
    }
}
