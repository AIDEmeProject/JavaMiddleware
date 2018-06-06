package metrics;

import data.LabeledData;
import learner.Learner;

public class TargetSetAccuracyCalculator implements MetricCalculator {
    @Override
    public Metrics compute(LabeledData data, Learner learner) {
        return PositiveSetAccuracy.compute(data.getLabeledRows(), data.getY());
    }
}
