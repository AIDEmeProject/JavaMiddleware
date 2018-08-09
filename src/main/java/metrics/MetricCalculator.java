package metrics;

import classifier.Classifier;
import data.LabeledDataset;
import user.User;

/**
 * This is an interface for all classes specialized in computing metrics from labeled data.
 */
public interface MetricCalculator {
    /**
     * Computes a Metric from the labeled data and activeLearner objects.
     * @param data: labeled data
     * @param user: user instance
     * @param classifier: classifier
     * @return computed metric
     */
    MetricStorage compute(LabeledDataset data, User user, Classifier classifier);
}
