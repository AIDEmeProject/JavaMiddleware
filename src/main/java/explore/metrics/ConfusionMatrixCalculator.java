package explore.metrics;

import data.LabeledDataset;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
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
    public MetricStorage compute(LabeledDataset data, Label[] trueLabels) {
        Classifier classifier = learner.fit(data.getLabeledPoints());
        return compute(trueLabels, classifier.predict(data.getAllPoints()));
    }

    /**
     * Computes a ConfusionMatrix from the true labels and predicted labels arrays.
     *
     * @param trueLabels: array of true labels
     * @param predictedLabels: array of predicted labels
     * @return a confusion matrix
     * @throws IllegalArgumentException if inputs have incompatible dimensions or are 0-length arrays
     */
    public ConfusionMatrix compute(Label[] trueLabels, Label[] predictedLabels){
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
