package explore;

import metrics.ConfusionMatrix;

import java.util.Collection;

public class ExplorationResult {
    private Collection<Integer> labeledRows;
    private Collection<ConfusionMatrix> accuracyMetrics;

    public ExplorationResult(Collection<Integer> labeledRows, Collection<ConfusionMatrix> accuracyMetrics) {
        this.labeledRows = labeledRows;
        this.accuracyMetrics = accuracyMetrics;
    }

    public Collection<Integer> getLabeledRows() {
        return labeledRows;
    }

    public Collection<ConfusionMatrix> getAccuracyMetrics() {
        return accuracyMetrics;
    }
}
