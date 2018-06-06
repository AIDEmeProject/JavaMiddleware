package metrics;

import data.LabeledData;
import learner.Learner;

public class ConfusionMatrixCalculator implements MetricCalculator{
    /**
     * Compute a ConfusionMatrix from the true labels and predicted labels.
     * @param data: dataset
     * @param learner: active learner
     * @return a confusion matrix
     */
    @Override
    public Metrics compute(LabeledData data, Learner learner) {
        return ConfusionMatrix.compute(data.getY(), learner.predict(data));
    }
}
