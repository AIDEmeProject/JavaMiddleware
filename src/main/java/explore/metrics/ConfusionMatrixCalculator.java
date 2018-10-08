package explore.metrics;

import data.DataPoint;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import explore.user.UserLabel;
import machinelearning.threesetmetric.ExtendedLabel;
import utils.Validator;

/**
 * This module is a factory for ConfusionMatrix objects.
 *
 * @see ConfusionMatrix
 */
public class ConfusionMatrixCalculator implements MetricCalculator {
    private Learner learner;

    public ConfusionMatrixCalculator(Learner learner) {
        this.learner = learner;
    }

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        UserLabel[] trueLabels = user.getLabel(data.getAllPoints());  // TODO: can we avoid recomputing these labels

        Classifier classifier = learner.fit(data.getLabeledPoints());

        Label[] labels = data.getAllPoints().stream()
                .map(x -> getLabel(x, data, classifier))
                .toArray(Label[]::new);

        return compute(trueLabels, labels);
    }

    private Label getLabel(DataPoint point, PartitionedDataset partitionedDataset, Classifier classifier){
        ExtendedLabel extendedLabel = partitionedDataset.getLabel(point);
        return extendedLabel.isUnknown() ? classifier.predict(point) : extendedLabel.toLabel();
    }

    /**
     * Computes a ConfusionMatrix from the true labels and predicted labels arrays.
     *
     * @param trueLabels: array of true labels
     * @param predictedLabels: array of predicted labels
     * @return a confusion matrix
     * @throws IllegalArgumentException if inputs have incompatible dimensions or are 0-length arrays
     */
    public ConfusionMatrix compute(UserLabel[] trueLabels, UserLabel[] predictedLabels){
        Validator.assertEqualLengths(trueLabels, predictedLabels);
        Validator.assertNotEmpty(trueLabels);

        int truePositives = 0, trueNegatives = 0, falseNegatives = 0, falsePositives = 0;

        for(int i=0; i < trueLabels.length; i++){
            if(predictedLabels[i].isPositive()){
                if(trueLabels[i].isPositive())
                    truePositives++;
                else
                    falsePositives++;
            }
            else {
                if(trueLabels[i].isPositive())
                    falseNegatives++;
                else
                    trueNegatives++;
            }
        }

        return new ConfusionMatrix(truePositives, trueNegatives, falsePositives, falseNegatives);
    }
}
