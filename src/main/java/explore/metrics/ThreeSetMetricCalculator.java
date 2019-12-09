package explore.metrics;

import data.PartitionedDataset;
import explore.user.User;
import machinelearning.threesetmetric.ExtendedLabel;

public class ThreeSetMetricCalculator implements MetricCalculator {
    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        double numPositivePoints = data.getKnownPoints().stream()
                .map(data::getLabel)
                .filter(ExtendedLabel::isPositive)
                .count();

        double numUncertainPoints = data.getUnknownSize();

        return new ThreeSetMetric(numPositivePoints, numUncertainPoints);
    }
}
