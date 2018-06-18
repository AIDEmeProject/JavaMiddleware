package active.activesearch;

import classifier.Classifier;
import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;

/**
 * Dummy class used for general classifiers / learners. Basically, it lets the full tree search to be performed.
 */
class DummyUpperBoundCalculator implements UpperBoundCalculator{

    private final Learner learner;

    public DummyUpperBoundCalculator(Learner learner) {
        this.learner = learner;
    }

    public Classifier fit(LabeledDataset data, int steps){
        return learner.fit(data.getLabeledPoints());
    }

    @Override
    public double upperBound(DataPoint point) {
        return Double.POSITIVE_INFINITY;
    }
}
