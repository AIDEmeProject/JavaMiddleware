package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.classifier.margin.MarginClassifier;
import utils.linalg.Vector;

public class MarginRanker implements Ranker {
    private MarginClassifier marginClassifier;

    public MarginRanker(MarginClassifier marginClassifier) {
        this.marginClassifier = marginClassifier;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        return marginClassifier.margin(unlabeledData.getData()).iApplyMap(Math::abs);
    }
}
