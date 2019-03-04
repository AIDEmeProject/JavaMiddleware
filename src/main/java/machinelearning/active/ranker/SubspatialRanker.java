package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.subspatial.ConnectionFunction;
import machinelearning.classifier.SubspatialClassifier;
import utils.linalg.Vector;

/**
 * A Subspatial Ranker contains {@link SubspatialClassifier} object, containing the fitted classifiers over each data
 * subspace. The informative score is computed by first computing the subspatial probabilities, and finally piecing together
 * these values through a {@link ConnectionFunction}, such as L1 score, PRODUCT, ENTROPY, ...
 */
public class SubspatialRanker implements Ranker {
    /**
     * Ranker objects for each subspace
     */
    private final SubspatialClassifier classifier;

    /**
     * Function computing the final score from each subspace probability
     */
    private final ConnectionFunction connectionFunction;

    public SubspatialRanker(SubspatialClassifier classifiers, ConnectionFunction connectionFunction) {
        this.classifier = classifiers;
        this.connectionFunction = connectionFunction;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        return connectionFunction.apply(classifier.probabilityAllSubspaces(unlabeledData));
    }
}
