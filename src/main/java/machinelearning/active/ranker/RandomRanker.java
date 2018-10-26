package machinelearning.active.ranker;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.active.Ranker;

public class RandomRanker implements Ranker {

    /**
     * @return a random a point from the input collection
     */
    @Override
    public DataPoint top(IndexedDataset unlabeledSet) {
        return unlabeledSet.sample(1).get(0);
    }
}
