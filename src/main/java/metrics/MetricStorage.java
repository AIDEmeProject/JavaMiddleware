package metrics;

import java.util.Map;

/**
 * This is an interface for objects storing one or more accuracy metrics. Factory methods are provided by MetricCalculator
 * classes also.
 *
 * @see MetricCalculator
 */
public interface MetricStorage {
    /**
     * @return all metrics contained in the object in a Metrics object.
     */
    Map<String, Double> getMetrics();
}
