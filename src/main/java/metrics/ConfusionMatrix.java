package metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * This module is responsible for, given true labels and predicted labels arrays, compute its Confusion Matrix and
 * related metrics. In particular, we provide methods for computing Precision, Recall, Accuracy and F-Score.
 *
 * We only support the binary classification scenario (0 and 1 labels).
 * @see <a href="https://en.wikipedia.org/wiki/Confusion_matrix">Confusion Matrix Wiki</a>
 * @author luciano
 */
public class ConfusionMatrix {
    private final double truePositives;
    private final double trueNegatives;
    private final double falsePositives;
    private final double falseNegatives;

    private ConfusionMatrix(double truePositives, double trueNegatives, double falsePositives, double falseNegatives) {
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
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
    public static ConfusionMatrix compute(int[] trueLabels, int[] predictedLabels){
        if(trueLabels.length != predictedLabels.length){
            throw new IllegalArgumentException("Incompatible sizes: " + trueLabels.length + ", " + predictedLabels.length);
        }

        if(trueLabels.length == 0){
            throw new IllegalArgumentException("Received empty array as input.");
        }

        double truePositives = 0, trueNegatives = 0, falseNegatives = 0, falsePositives = 0;

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

    /**
     * @return true positives (prediction = label = 1)
     */
    public double truePositives() {
        return truePositives;
    }

    /**
     * @return true negatives (prediction = label = 0)
     */
    public double trueNegatives() {
        return trueNegatives;
    }

    /**
     * @return false positives (prediction = 1, label = 0)
     */
    public double falsePositives() {
        return falsePositives;
    }

    /**
     * @return false negatives (prediction = 0, label = 1)
     */
    public double falseNegatives() {
        return falseNegatives;
    }

    /**
     * @return classification accuracy ( # correct predictions / # total )
     */
    public double accuracy(){
        return trueDivide(truePositives + trueNegatives, truePositives + trueNegatives + falsePositives + falseNegatives);
    }

    /**
     * @return classification precision (TP / TP + FP)
     */
    public double precision(){
        return trueDivide(truePositives, truePositives + falsePositives);
    }

    /**
     * @return classification precision (TP / TP + FN)
     */
    public double recall(){
        return trueDivide(truePositives, truePositives + falseNegatives);
    }

    /**
     * @return classification F-score (harmonic mean of precision and recall)
     */
    public double fscore(){
        return trueDivide(2*truePositives, 2*truePositives + falsePositives + falseNegatives);
    }

    private double trueDivide(double a, double b){
        return (b == 0) ? 0 : a / b;
    }

    /**
     * @return Map object containing all metrics stored in the ConfusionMatrix object.
     */
    public Map<String, Double> getMetrics(){
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("truePositives", truePositives());
        metrics.put("trueNegatives", trueNegatives());
        metrics.put("falsePositives", falsePositives());
        metrics.put("falseNegatives", falseNegatives());
        metrics.put("precision", precision());
        metrics.put("recall", recall());
        metrics.put("fscore", fscore());
        return metrics;
    }
}
