package metrics;

import classifier.Classifier;
import data.LabeledDataset;
import data.LabeledPoint;
import user.User;

import java.util.Collection;

/**
 * Factory method for TargetSetAccuracy class.
 *
 * @see TargetSetAccuracy
 */
public class TargetSetAccuracyCalculator implements MetricCalculator {
    /**
     * Compute target set accuracy from the true labels array and the collection of labeled rows so far.
     * @param labeledPoints: collection of labeled points
     * @param y: true labels array
     * @return TargetSetAccuracy object
     */
    public TargetSetAccuracy compute(Collection<LabeledPoint> labeledPoints, int[] y){
        int numberOfTargetsRetrieved = 0;
        for (LabeledPoint point : labeledPoints){
            numberOfTargetsRetrieved += point.getLabel();
        }

        int totalNumberOfTargets = 0;
        for (int label : y){
            totalNumberOfTargets += label;
        }

        return new TargetSetAccuracy(numberOfTargetsRetrieved, totalNumberOfTargets);
    }

    @Override
    public MetricStorage compute(LabeledDataset data, User user, Classifier classifier) {
        return compute(data.getLabeledPoints(), user.getLabel(data.getAllPoints()));
    }
}
