package active.activesearch;

import data.LabeledDataset;

/**
 * Dummy class used for general classifiers / learners. Basically, it lets the full tree search to be performed.
 */
class DummyUpperBoundCalculator implements UpperBoundCalculator{

    public void fit(LabeledDataset data, int steps){

    }

    @Override
    public double upperBound(double proba) {
        return Double.POSITIVE_INFINITY;
    }
}
