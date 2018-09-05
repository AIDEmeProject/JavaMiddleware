package explore.metrics;

import data.LabeledDataset;
import data.LabeledPoint;
import machinelearning.classifier.Label;

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
    public TargetSetAccuracy compute(Collection<LabeledPoint> labeledPoints, Label[] y){
        int numberOfTargetsRetrieved = 0;
        for (LabeledPoint point : labeledPoints){
            numberOfTargetsRetrieved += point.getLabel().asBinary();
        }

        int totalNumberOfTargets = 0;
        for (Label label : y){
            totalNumberOfTargets += label.asBinary();
        }

        return new TargetSetAccuracy(numberOfTargetsRetrieved, totalNumberOfTargets);
    }

    @Override
    public MetricStorage compute(LabeledDataset data, Label[] trueLabels) {
        return compute(data.getLabeledPoints(), trueLabels);
    }
}
