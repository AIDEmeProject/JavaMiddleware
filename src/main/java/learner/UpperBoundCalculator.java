package learner;

import classifier.BoundedClassifier;
import data.LabeledData;

class UpperBoundCalculator {
    private double u0, u1;

    public void fit(LabeledData data, BoundedClassifier classifier, int steps){
        u0 = optimalUtilityUpperBound(data, classifier,steps-1, 0);
        u1 = optimalUtilityUpperBound(data, classifier,steps-1, 1);
    }

    /**
     * Helper function for computing the upper bound on the utility. It is also a recursive function, but it only needs to
     * be computed once, and not for every point in the unlabeled set. Refer to [2] for details.
     * @param steps: remaining steps to run
     * @param maxLabeledPoints: maximum number of positive points that can be added to current labeled set
     * @return upper bound on optimal utility
     */
    private double optimalUtilityUpperBound(LabeledData data, BoundedClassifier classifier, int steps, int maxLabeledPoints){
        double pStar = classifier.computeProbabilityUpperBound(data, maxLabeledPoints);

        if (steps <= 1){
            return pStar;
        }

        double positiveUpperBound = optimalUtilityUpperBound(data, classifier, steps - 1, maxLabeledPoints + 1);
        double negativeUpperBound = optimalUtilityUpperBound(data, classifier, steps - 1, maxLabeledPoints);

        return (positiveUpperBound + 1) * pStar + negativeUpperBound * (1 - pStar);
    }

    public double upperBound(double proba){
        return (u1 + 1) * proba + u0 * (1 - proba);
    }
}