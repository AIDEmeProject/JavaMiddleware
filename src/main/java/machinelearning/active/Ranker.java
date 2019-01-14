package machinelearning.active;

import data.DataPoint;
import data.IndexedDataset;
import utils.linalg.Vector;

public interface Ranker {
    /**
     * @param unlabeledData: data to compute ranking scores. The lower, the "more informative" a data point is considered
     * @return the ranking scores for each DataPoint in @code{unlabeledData}
     */
    Vector score(IndexedDataset unlabeledData);

    /**
     * @param unlabeledSet collection of unlabeled points
     * @return the "most informative" point in the input collection
     * @throws IllegalArgumentException if unlabeledSet is empty
     */
    default DataPoint top(IndexedDataset unlabeledSet) {
        return unlabeledSet.get(score(unlabeledSet).argmin());
    }
}
