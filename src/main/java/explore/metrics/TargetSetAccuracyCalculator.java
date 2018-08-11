package explore.metrics;

import machinelearning.classifier.Classifier;
import data.LabeledDataset;
import data.LabeledPoint;
import explore.user.User;

import java.util.Collection;

/**
 * Factory method for {@link TargetSetAccuracy} class.
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
