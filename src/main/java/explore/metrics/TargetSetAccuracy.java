package explore.metrics;

import utils.Validator;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * @param numberOfTargetsRetrieved: Number of targets retrieved
     * @param totalNumberOfTargets:
     * @throws IllegalArgumentException if totalNumberOfTargets is not positive, or if numberOfTargetsRetrieved is negative or larger than totalNumberOfTargets
     */
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
    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("NumberOfTargetsRetrieved", (double) getNumberOfTargetsRetrieved());
        metrics.put("TargetSetAccuracy", targetSetAccuracy());
        return metrics;
    }
}
