package explore.metrics;

import data.PartitionedDataset;
import explore.user.User;
import explore.user.UserLabel;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import utils.Validator;

/**
 * This module is a factory for ConfusionMatrix objects.
 *
 * @see ConfusionMatrix
 */
public class ConfusionMatrixCalculator implements MetricCalculator {
    protected Learner learner;

    public ConfusionMatrixCalculator(Learner learner) {
        this.learner = learner;
    }

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        UserLabel[] trueLabels = user.getLabel(data.getAllPoints());  // TODO: can we avoid recomputing these labels
        Classifier classifier = learner.fit(data.getLabeledPoints());
        return compute(trueLabels, data.predictLabels(classifier));
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

    @Override
    public void setFactorizationStructure(int[][] partition) {
        learner.setFactorizationStructure(partition);
    }
}
