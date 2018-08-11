package explore.metrics;

import machinelearning.classifier.Classifier;
import data.LabeledDataset;
import explore.user.User;
import utils.Validator;

/**
 * This module is a factory for ConfusionMatrix objects.
 *
 * @see ConfusionMatrix
 */
public class ConfusionMatrixCalculator implements MetricCalculator{
    /**
     * Compute a ConfusionMatrix from the labeled dataset and activeLearner objects.
     * @param data: dataset containing both features X and true labels y
     * @param classifier: classifier object
     * @return a confusion matrix
     */
    @Override
    public MetricStorage compute(LabeledDataset data, User user, Classifier classifier) {
        return compute(user.getLabel(data.getAllPoints()), classifier.predict(data.getAllPoints()));
    }

    /**
     * Computes a ConfusionMatrix from the true labels and predicted labels arrays.
     *
     * @param trueLabels: array of true labels
     * @param predictedLabels: array of predicted labels
     * @return a confusion matrix
     * @throws IllegalArgumentException if inputs have incompatible dimensions, are 0-length arrays, or contain any value
     * different from 1 or 0.
     */
    public ConfusionMatrix compute(int[] trueLabels, int[] predictedLabels){
        // validate input
        Validator.assertEqualLengths(trueLabels, predictedLabels);
        Validator.assertNotEmpty(trueLabels);

        // compute metrics
        int truePositives = 0, trueNegatives = 0, falseNegatives = 0, falsePositives = 0;

        for(int i=0; i < trueLabels.length; i++){
            if(trueLabels[i] == 1 && predictedLabels[i] == 1){
                truePositives++;
            }
            else if(trueLabels[i] == 1 && predictedLabels[i] == 0){
                falseNegatives++;
            }
            else if(trueLabels[i] == 0 && predictedLabels[i] == 1){
                falsePositives++;
            }
            else if(trueLabels[i] == 0 && predictedLabels[i] == 0){
                trueNegatives++;
            }
            else{
                throw new IllegalArgumentException(
                        "Only 1 and 0 labels are supported, received " + trueLabels[i] + " and " + predictedLabels[i]);
            }
        }

        return new ConfusionMatrix(truePositives, trueNegatives, falsePositives, falseNegatives);
    }
}
