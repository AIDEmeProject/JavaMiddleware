package metrics;

import explore.Metrics;

/**
 * This module is responsible for storing the confusion matrix values. In particular, we provide methods for computing
 * other classification metrics like Precision, Recall, Accuracy, and F-Score.
 *
 * We only support the binary classification (0 and 1 labels).
 * @see <a href="https://en.wikipedia.org/wiki/Confusion_matrix">Confusion Matrix Wiki</a>
 * @see ConfusionMatrixCalculator
 * @author luciano
 */
public class ConfusionMatrix implements MetricStorage {
    private final int truePositives;
    private final int trueNegatives;
    private final int falsePositives;
    private final int falseNegatives;

    public ConfusionMatrix(int truePositives, int trueNegatives, int falsePositives, int falseNegatives) {
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
    }

    /**
     * @return true positives (prediction = label = 1)
     */
    public int truePositives() {
        return truePositives;
    }

    /**
     * @return true negatives (prediction = label = 0)
     */
    public int trueNegatives() {
        return trueNegatives;
    }

    /**
     * @return false positives (prediction = 1, label = 0)
     */
    public int falsePositives() {
        return falsePositives;
    }

    /**
     * @return false negatives (prediction = 0, label = 1)
     */
    public int falseNegatives() {
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
    public Metrics getMetrics(){
        Metrics metrics = new Metrics();
        metrics.add("TruePositives", (double) truePositives());
        metrics.add("TrueNegatives", (double) trueNegatives());
        metrics.add("FalsePositives", (double) falsePositives());
        metrics.add("FalseNegatives", (double) falseNegatives());
        metrics.add("Precision", precision());
        metrics.add("Recall", recall());
        metrics.add("Fscore", fscore());
        return metrics;
    }
}
