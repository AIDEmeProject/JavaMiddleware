package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.subspatial.LossFunction;
import machinelearning.classifier.SubspatialClassifier;
import utils.linalg.Vector;

/**
 * A Subspatial Ranker contains {@link SubspatialClassifier} object, containing the fitted classifiers over each data
 * subspace. The informative score is computed by first computing the subspatial probabilities, and finally piecing together
 * these values through a {@link LossFunction}, such as L1 score, PRODUCT, ENTROPY, ...
 */
public class SubspatialRanker implements Ranker {
    /**
     * Ranker objects for each subspace
     */
    private final SubspatialClassifier classifier;

    /**
     * Function computing the final score from each subspace probability
     */
    private final LossFunction lossFunction;

    public SubspatialRanker(SubspatialClassifier classifiers, LossFunction lossFunction) {
        this.classifier = classifiers;
        this.lossFunction = lossFunction;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        return lossFunction.apply(classifier.probabilityAllSubspaces(unlabeledData));
    }
}
