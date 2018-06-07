package metrics;

import explore.Metrics;

/**
 * This is an interface for objects storing one or more accuracy metrics. Factory methods are provided by MetricCalculator
 * classes also.
 *
 * @see MetricCalculator
 */
public interface MetricStorage {
    /**
     * @return all metrics contained in the object in a Map object. Keys are the metric's name, the the values the
     * corresponding computed metric values.
     */
    Metrics getMetrics();
}
