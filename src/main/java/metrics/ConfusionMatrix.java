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
public class ConfusionMatrix implements Metrics{
    private final int truePositives;
    private final int trueNegatives;
    private final int falsePositives;
    private final int falseNegatives;

    ConfusionMatrix(int truePositives, int trueNegatives, int falsePositives, int falseNegatives) {
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
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
