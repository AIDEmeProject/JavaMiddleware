package metrics;

import explore.Metrics;
import utils.Validator;

/**
 * This module stores the number of target elements retrieved so far, as well as the total number of targets in the dataset.
 * This metric mostly used in the Active Search framework.
 *
 * @see TargetSetAccuracyCalculator
 */
public class TargetSetAccuracy implements MetricStorage {
    /**
     * Number of targets retrieved so far
     */
    private final int numberOfTargetsRetrieved;

    /**
     * Total number of targets in the dataset
     */
    private final int totalNumberOfTargets;

    public TargetSetAccuracy(int numberOfTargetsRetrieved, int totalNumberOfTargets) {
        Validator.assertNonNegative(numberOfTargetsRetrieved);
        Validator.assertPositive(totalNumberOfTargets);

        if (numberOfTargetsRetrieved > totalNumberOfTargets){
            throw new IllegalArgumentException("Number of retrieved targets is larger than target set size.");
        }

        this.numberOfTargetsRetrieved = numberOfTargetsRetrieved;
        this.totalNumberOfTargets = totalNumberOfTargets;
    }

    public int getTotalNumberOfTargets() {
        return totalNumberOfTargets;
    }

    public int getNumberOfTargetsRetrieved() {
        return numberOfTargetsRetrieved;
    }

    /**
     * @return percentage of target set retrieved so far
     */
    public double targetSetAccuracy(){
        return (double) numberOfTargetsRetrieved / totalNumberOfTargets;
    }

    @Override
    public Metrics getMetrics() {
        Metrics metrics = new Metrics();
        metrics.add("NumberOfTargetsRetrieved", (double) getNumberOfTargetsRetrieved());
        metrics.add("TargetSetAccuracy", targetSetAccuracy());
        return metrics;
    }
}
