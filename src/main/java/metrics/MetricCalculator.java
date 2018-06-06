package metrics;

import data.LabeledData;
import learner.Learner;

public interface MetricCalculator {
    Metrics compute(LabeledData data, Learner learner);
}
