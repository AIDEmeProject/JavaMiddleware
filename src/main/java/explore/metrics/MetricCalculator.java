package explore.metrics;

import data.LabeledDataset;
import machinelearning.classifier.Label;

/**
 * This is an interface for all classes specialized in computing metrics  from labeled data.
 */
public interface MetricCalculator {
    /**
     * Computes a Metric from the labeled data and activeLearner objects.
     * @param data: labeled data
     * @param trueLabels: true labels
     * @return computed metric
     */
    MetricStorage compute(LabeledDataset data, Label[] trueLabels);
}
