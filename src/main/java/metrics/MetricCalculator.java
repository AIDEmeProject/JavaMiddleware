package metrics;

import classifier.Classifier;
import data.LabeledData;

/**
 * This is an interface for all classes specialized in computing metrics from labeled data.
 */
public interface MetricCalculator {
    /**
     * Computes a Metric from the labeled data and activeLearner objects.
     * @param data: labeled data
     * @param classifier: classifier
     * @return computed metric
     */
    MetricStorage compute(LabeledData data, Classifier classifier);
}
