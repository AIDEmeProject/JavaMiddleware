package explore.metrics;

import java.util.HashMap;
import java.util.Map;

public class SubspatialConfusionMatrix implements MetricStorage {
    private final ConfusionMatrix[] confusionMatrices;
    private static final String[] METRICS = new String[]{"Fscore", "Precision", "Recall"};

    public SubspatialConfusionMatrix(ConfusionMatrix[] confusionMatrices) {
        this.confusionMatrices = confusionMatrices;
    }

    @Override
    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>();


        for (int i = 0; i < confusionMatrices.length; i++) {
            Map<String, Double> subspaceMetrics = confusionMatrices[i].getMetrics();

            for (String metric : METRICS) {
                metrics.put(metric + "_" + i, subspaceMetrics.get(metric));
            }
        }
        return metrics;
    }
}
