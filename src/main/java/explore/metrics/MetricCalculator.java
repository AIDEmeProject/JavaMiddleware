package explore.metrics;

import data.PartitionedDataset;
import explore.SubspatialDecompositionInjectable;
import explore.user.User;

/**
 * This is an interface for all classes specialized in computing metrics  from labeled data.
 */
public interface MetricCalculator extends SubspatialDecompositionInjectable {
    /**
     * Computes a Metric from the labeled data and activeLearner objects.
     * @param data: labeled data
     * @param user: user oracle
     * @return computed metric
     */
    MetricStorage compute(PartitionedDataset data, User user);
}
