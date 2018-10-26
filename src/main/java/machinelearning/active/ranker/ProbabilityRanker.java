package machinelearning.active.ranker;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import utils.LinearSearch;

import java.util.Objects;

public class ProbabilityRanker implements Ranker {
    private Classifier classifier;

    public ProbabilityRanker(Classifier classifier) {
        this.classifier = Objects.requireNonNull(classifier);
    }

    /**
     * @return the point in the collection whose predicted probability is the closest to 0.5
     */
    @Override
    public DataPoint top(IndexedDataset unlabeledSet) {
        return LinearSearch.findMinimizer(unlabeledSet, pt -> Math.abs(classifier.probability(pt) - 0.5));
    }
}
