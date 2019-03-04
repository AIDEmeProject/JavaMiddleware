package machinelearning.active.learning;

import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.SubspatialRanker;
import machinelearning.active.ranker.subspatial.ConnectionFunction;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.SubspatialLearner;

/**
 * A Subspatial Active Learner decomposes the learning task across each feature subspace. Basically, one particular
 * Learner is fit over each data subspace using the partial label information, and a final {@link Ranker} object returned
 * which pieces together all fitted {@link Classifier} objects.
 */
public class SubspatialActiveLearner implements ActiveLearner {

    /**
     * Active Learners to be fit to each subspace
     */
    private final SubspatialLearner subspatialLearner;

    /**
     * Function connecting the subspace probabilities into a final informativeness score
     */
    private final ConnectionFunction connectionFunction;

    public SubspatialActiveLearner(SubspatialLearner subspatialLearner, ConnectionFunction connectionFunction) {
        this.subspatialLearner = subspatialLearner;
        this.connectionFunction = connectionFunction;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        return new SubspatialRanker(subspatialLearner.fit(labeledPoints), connectionFunction);
    }
}
