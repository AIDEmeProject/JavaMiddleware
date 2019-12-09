package machinelearning.classifier;

import data.LabeledDataset;
import utils.linalg.Vector;

public interface Learner {

    /**
     * Train a classification model over training data. Only the labeled points are considered for training.
     * @param labeledPoints: collection of labeled points
     * @throws IllegalArgumentException if labeledPoints is empty
     * @return a Classifier trained over the labeledPoints
     */
    Classifier fit(LabeledDataset labeledPoints);

    /**
     * @param labeledPoints collection of labeled points
     * @param sampleWeights weight of each data point
     * @throws IllegalArgumentException if sampleWeights and labeledPoints have incompatible sizes
     * @return a Classifier trained over the labeledPoints
     */
    default Classifier fit(LabeledDataset labeledPoints, Vector sampleWeights) {
        return fit(labeledPoints);
    }
}
