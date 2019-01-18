package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import utils.linalg.Vector;

import java.util.Objects;

public class ProbabilityRanker implements Ranker {
    private Classifier classifier;

    public ProbabilityRanker(Classifier classifier) {
        this.classifier = Objects.requireNonNull(classifier);
    }

    /**
     * @return |p(x) - 0.5|, for all x
     */
    @Override
    public Vector score(IndexedDataset unlabeledData) {
        return classifier.probability(unlabeledData).iScalarSubtract(0.5).iApplyMap(Math::abs);
    }
}
