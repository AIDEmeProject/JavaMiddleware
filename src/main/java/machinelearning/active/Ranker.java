package machinelearning.active;

import data.DataPoint;
import data.IndexedDataset;

public interface Ranker {
    /**
     * @param unlabeledSet collection of unlabeled points
     * @return the "most informative" point in the input collection
     * @throws IllegalArgumentException if unlabeledSet is empty
     */
    DataPoint top(IndexedDataset unlabeledSet);
}