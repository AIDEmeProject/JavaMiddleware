package explore;

import metrics.ConfusionMatrix;

import java.util.Collection;

public class ExplorationResult {
    private Collection<Integer> labeledRows;
    private Collection<ConfusionMatrix> accuracyMetrics;
    private Collection<Double> positiveSetAccuracy;

    public ExplorationResult(Collection<Integer> labeledRows, Collection<ConfusionMatrix> accuracyMetrics, Collection<Double> positiveSetAccuracy) {
        this.labeledRows = labeledRows;
        this.accuracyMetrics = accuracyMetrics;
        this.positiveSetAccuracy = positiveSetAccuracy;
    }

    public Collection<Integer> getLabeledRows() {
        return labeledRows;
    }

    public Collection<ConfusionMatrix> getAccuracyMetrics() {
        return accuracyMetrics;
    }

    public Collection<Double> getPositiveSetAccuracy() {
        return positiveSetAccuracy;
    }
}
