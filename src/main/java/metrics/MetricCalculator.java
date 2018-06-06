package metrics;

import data.LabeledData;
import learner.Learner;

/**
 * This is an interface for all classes specialized in computing metrics from labeled data.
 */
public interface MetricCalculator {
    /**
     * Computes a Metric from the labeled data and learner objects.
     * @param data: labeled data
     * @param learner: active learning or active search model
     * @return computed metric
     */
    Metrics compute(LabeledData data, Learner learner);
}
