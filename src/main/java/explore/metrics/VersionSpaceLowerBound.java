package explore.metrics;

import utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class VersionSpaceLowerBound implements MetricStorage {
    private final int positiveCount, negativeCount, predictedPositives, total;

    public VersionSpaceLowerBound(int positiveCount, int negativeCount, int predictedPositives, int total) {
        Validator.assertNonNegative(positiveCount);
        Validator.assertNonNegative(negativeCount);
        Validator.assertNonNegative(predictedPositives);
        Validator.assertPositive(total);

        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.predictedPositives = predictedPositives;
        this.total = total;
    }

    @Override
    public Map<String, Double> getMetrics() {
        double precisionEstimate = (double) positiveCount / predictedPositives;
        double recallEstimate = (double) positiveCount / (total - negativeCount);
        double lowerBound = precisionEstimate * recallEstimate / (precisionEstimate + recallEstimate);

        System.out.println("lower bound: " + lowerBound);

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("PositiveCount", (double) positiveCount);
        metrics.put("NegativeCount", (double) negativeCount);
        metrics.put("PredictedPositives", (double) predictedPositives);
        metrics.put("PrecisionEstimate", precisionEstimate);
        metrics.put("RecallEstimate", recallEstimate);
        metrics.put("VersionSpaceLowerBound", lowerBound);
        return metrics;
    }
}
