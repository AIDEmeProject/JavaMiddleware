package machinelearning.active.ranker;

import data.DataPoint;
import machinelearning.active.Ranker;
import machinelearning.classifier.margin.MarginClassifier;
import utils.LinearSearch;

import java.util.Collection;

public class MarginRanker implements Ranker {
    private MarginClassifier marginClassifier;

    public MarginRanker(MarginClassifier marginClassifier) {
        this.marginClassifier = marginClassifier;
    }

    @Override
    public DataPoint top(Collection<DataPoint> unlabeledSet) {
        return LinearSearch.findMinimizer(unlabeledSet, pt -> Math.abs(marginClassifier.margin(pt)));
    }
}
