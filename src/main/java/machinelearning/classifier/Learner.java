package machinelearning.classifier;

import data.LabeledDataset;

public interface Learner {

    /**
     * Train a classification model over training data. Only the labeled points are considered for training.
     * @param labeledPoints: collection of labeled points
     * @throws IllegalArgumentException if labeledPoints is empty
     * @return a Classifier trained over the labeledPoints
     */
    Classifier fit(LabeledDataset labeledPoints);
}
