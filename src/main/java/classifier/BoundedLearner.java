package classifier;

import data.LabeledPoint;

import java.util.Collection;

/**
 * Fits and returns a bounded classifier
 */
public interface BoundedLearner extends Learner {
    BoundedClassifier fit(Collection<LabeledPoint> labeledPoints);
}
