package explore.metrics;

import data.LabeledDataset;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import utils.linalg.Matrix;

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

        Matrix labeledData = labeledDataset.getData();
        Label[] predictedLabels = learner.fit(labeledDataset).predict(labeledData);

        return super.compute(labeledDataset.getLabels(), predictedLabels);
    }
}
