package metrics;

import data.LabeledData;
import learner.Learner;

public class ConfusionMatrixCalculator implements MetricCalculator{
    /**
     * Compute a ConfusionMatrix from the true labels and predicted labels.
     * @param data: dataset
     * @param learner: active learner
     * @return a confusion matrix
     */
    @Override
    public Metrics compute(LabeledData data, Learner learner) {
        return compute(data.getY(), learner.predict(data));
    }

    /**
     * Factory method for Confusion Matrix. By being static, we separate the creation logic from each object, allowing
     * for each ConfusionMatrix object to be immutable.
     *
     * @param trueLabels: array of true labels
     * @param predictedLabels: array of predicted labels
     * @return a confusion matrix
     * @throws IllegalArgumentException if inputs have incompatible dimensions, are 0-length arrays, or contain any value
     * different from 1 or 0.
     */
    public ConfusionMatrix compute(int[] trueLabels, int[] predictedLabels){
        if(trueLabels.length != predictedLabels.length){
            throw new IllegalArgumentException("Incompatible sizes: " + trueLabels.length + ", " + predictedLabels.length);
        }

        if(trueLabels.length == 0){
            throw new IllegalArgumentException("Received empty array as input.");
        }

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