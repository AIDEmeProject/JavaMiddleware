package classifier;

import data.DataPoint;
import data.LabeledPoint;

import java.util.Collection;

public interface Learner {

    /**
     * Initialized internal data structures of the learner from a collection of data points. In most cases it is not necessary.
     * @param points: data points
     */
    default void initialize(Collection<DataPoint> points){

    }

    /**
     * Train a classification model over training data. Only the labeled points are considered for training.
     * @param labeledPoints: collection of labeled points
     * @throws IllegalArgumentException if labeledPoints is empty
     * @return a Classifier trained over the labeledPoints
     */
    Classifier fit(Collection<LabeledPoint> labeledPoints);
}
