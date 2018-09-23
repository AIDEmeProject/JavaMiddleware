package explore.metrics;

import data.PartitionedDataset;
import explore.user.User;
import machinelearning.threesetmetric.ExtendedLabel;

public class ThreeSetMetricCalculator implements MetricCalculator {
    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        double numPositivePoints = data.getKnownPoints().stream()
                .map(data::getLabel)
                .filter(x -> x == ExtendedLabel.POSITIVE)
                .count();

        double numUncertainPoints = data.getUncertainPoints().size();

        return new ThreeSetMetric(numPositivePoints, numUncertainPoints);
    }
}
