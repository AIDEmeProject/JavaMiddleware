package explore.metrics;

import data.LabeledDataset;
import explore.user.User;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import utils.Validator;

/**
 * This module is a factory for ConfusionMatrix objects.
 *
 * @see ConfusionMatrix
 */
public class ConfusionMatrixCalculator implements MetricCalculator {
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
    public ConfusionMatrix compute(Label[] trueLabels, Label[] predictedLabels){
        // validate input
        Validator.assertEqualLengths(trueLabels, predictedLabels);
        Validator.assertNotEmpty(trueLabels);

        // compute metrics
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
