package explore.metrics;

import utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class ThreeSetMetric implements MetricStorage {
    private double numberOfPositivePoints;
    private double numberOfUncertainPoints;

    public ThreeSetMetric(double numberOfPositivePoints, double numberOfUncertainPoints) {
        Validator.assertNonNegative(numberOfPositivePoints);
        Validator.assertNonNegative(numberOfUncertainPoints);

        this.numberOfPositivePoints = numberOfPositivePoints;
        this.numberOfUncertainPoints = numberOfUncertainPoints;
    }

    public double accuracy() {
        return (numberOfPositivePoints) / (numberOfPositivePoints + numberOfUncertainPoints);
    }

    @Override
    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>(1);
        metrics.put("ThreeSetMetric", accuracy());
        return metrics;
    }
}
