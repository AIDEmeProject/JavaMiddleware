package active.activesearch;

import data.LabeledData;

/**
 * Dummy class used for general classifiers / learners. Basically, it lets the full tree search to be performed.
 */
class DummyUpperBoundCalculator implements UpperBoundCalculator{

    public void fit(LabeledData data, int steps){
        // do nothing
    }

    public double upperBound(double proba){
        return Double.POSITIVE_INFINITY;
    }
}
