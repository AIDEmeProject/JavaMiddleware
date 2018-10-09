package machinelearning.active.ranker;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.classifier.margin.MarginClassifier;
import utils.LinearSearch;

public class MarginRanker implements Ranker {
    private MarginClassifier marginClassifier;

    public MarginRanker(MarginClassifier marginClassifier) {
        this.marginClassifier = marginClassifier;
    }

    @Override
    public DataPoint top(IndexedDataset unlabeledSet) {
        return LinearSearch.findMinimizer(unlabeledSet, pt -> Math.abs(marginClassifier.margin(pt)));
    }
}
