package metrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class computes the evolution of the % of labeled set which was retrieved
 */
public class TargetSetAccuracy implements Metrics {
    private int numberOfTargetsRetrieved;
    private int totalNumberOfTargets;

    public TargetSetAccuracy(int numberOfTargetsRetrieved, int totalNumberOfTargets) {
        this.numberOfTargetsRetrieved = numberOfTargetsRetrieved;
        this.totalNumberOfTargets = totalNumberOfTargets;
    }

    public static TargetSetAccuracy compute(Collection<Integer> rows, int[] y){
        int numberOfTargetsRetrieved = 0;
        for (Integer row : rows){
            numberOfTargetsRetrieved += y[row];
        }

        int totalNumberOfTargets = 0;
        for (int label : y){
            totalNumberOfTargets += label;
        }

        return new TargetSetAccuracy(numberOfTargetsRetrieved, totalNumberOfTargets);
    }

    public int getNumberOfTargetsRetrieved() {
        return numberOfTargetsRetrieved;
    }

    public double accuracy(){
        return (double) numberOfTargetsRetrieved / totalNumberOfTargets;
    }

    @Override
    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("numberOfTargetsRetreived", (double) getNumberOfTargetsRetrieved());
        metrics.put("targetSetAccuracy", accuracy());
        return metrics;
    }
}
