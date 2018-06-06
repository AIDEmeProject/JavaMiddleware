package metrics;

import data.LabeledData;
import learner.Learner;

import java.util.Collection;

/**
 * Factory method for TargetSetAccuracy class.
 *
 * @see TargetSetAccuracy
 */
public class TargetSetAccuracyCalculator implements MetricCalculator {
    @Override
    public Metrics compute(LabeledData data, Learner learner) {
        return compute(data.getLabeledRows(), data.getY());
    }

    /**
     * Compute target set accuracy from the true labels array and the collection of labeled rows so far.
     * @param rows: collection of labeled rows so far
     * @param y: true labels array
     * @return TargetSetAccuracy object
     */
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
}
