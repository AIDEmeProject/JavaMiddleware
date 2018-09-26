package machinelearning.classifier;

import data.LabeledPoint;

import java.util.Collection;

public interface Learner {

    /**
     * Train a classification model over training data. Only the labeled points are considered for training.
     * @param labeledPoints: collection of labeled points
     * @throws IllegalArgumentException if labeledPoints is empty
     * @return a Classifier trained over the labeledPoints
     */
    Classifier fit(Collection<LabeledPoint> labeledPoints);
}
