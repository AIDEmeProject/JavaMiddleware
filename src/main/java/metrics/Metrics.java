package metrics;

import java.util.Map;

/**
 * This is an interface for objects storing one or more accuracy metrics. Factory methods are provided by MetricCalculator
 * classes also.
 *
 * @see MetricCalculator
 */
public interface Metrics {
    /**
     * @return all metrics contained in the object in a Map object. Keys are the metric's name, the the values the
     * corresponding computed metric values.
     */
    Map<String, Double> getMetrics();
}
