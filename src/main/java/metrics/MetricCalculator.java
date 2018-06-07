package metrics;

import data.LabeledData;
import learner.ActiveLearner;

/**
 * This is an interface for all classes specialized in computing metrics from labeled data.
 */
public interface MetricCalculator {
    /**
     * Computes a Metric from the labeled data and activeLearner objects.
     * @param data: labeled data
     * @param activeLearner: active learning or active search model
     * @return computed metric
     */
    MetricStorage compute(LabeledData data, ActiveLearner activeLearner);
}
