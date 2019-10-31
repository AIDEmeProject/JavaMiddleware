package explore.metrics;

import data.LabeledDataset;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;

/**
 * Computes the confusion matrix over the labeled set. This is to be used as a sanity check for our implementation
 */
public class LabeledSetConfusionMatrixCalculator extends ConfusionMatrixCalculator {
    public LabeledSetConfusionMatrixCalculator(Learner learner) {
        super(learner);
    }

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        LabeledDataset labeledDataset = data.getLabeledPoints();

        Label[] predictedLabels = learner.fit(labeledDataset).predict(labeledDataset.getData());

        Label[] trueLabels = new Label[predictedLabels.length];
        for (int i = 0; i < trueLabels.length; i++) {
            trueLabels[i] = labeledDataset.getLabel(i).isPositive() ? Label.POSITIVE : Label.NEGATIVE;
        }

        return super.compute(trueLabels, predictedLabels);
    }
}
