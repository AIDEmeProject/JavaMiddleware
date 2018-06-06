package metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * This module stores the number of target elements retrieved so far, as well as the total number of targets in the dataset.
 * This metric mostly used in the Active Search framework.
 *
 * @see TargetSetAccuracyCalculator
 */
public class TargetSetAccuracy implements Metrics {
    /**
     * Number of targets retrieved so far
     */
    private int numberOfTargetsRetrieved;

    /**
     * Total number of targets in the dataset
     */
    private int totalNumberOfTargets;

    public TargetSetAccuracy(int numberOfTargetsRetrieved, int totalNumberOfTargets) {
        if (numberOfTargetsRetrieved < 0){
            throw new IllegalArgumentException("Number of targets retrieved must be non-negative.");
        }
        if (totalNumberOfTargets <= 0){
            throw new IllegalArgumentException("Target set size must be positive.");
        }
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
        metrics.put("numberOfTargetsRetrieved", (double) getNumberOfTargetsRetrieved());
        metrics.put("targetSetAccuracy", targetSetAccuracy());
        return metrics;
    }
}
