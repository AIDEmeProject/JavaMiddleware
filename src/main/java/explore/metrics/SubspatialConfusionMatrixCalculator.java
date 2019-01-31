package explore.metrics;

import data.LabeledDataset;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import machinelearning.classifier.SubspatialClassifier;
import machinelearning.classifier.SubspatialLearner;
import utils.Validator;

public class SubspatialConfusionMatrixCalculator implements MetricCalculator {

    private final Learner subspatialLearner;

    public SubspatialConfusionMatrixCalculator(SubspatialLearner subspatialLearner) {
        this.subspatialLearner = subspatialLearner;
    }

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        Label[][] trueLabels = user.getPartialLabels(data.getAllPoints());

        LabeledDataset labeledDataset = data.getLabeledPoints();
        SubspatialClassifier subspatialClassifier = ((SubspatialLearner) subspatialLearner).fit(labeledDataset);
        Label[][] predictedLabels = subspatialClassifier.predictAllSubspaces(data.getAllPoints());

        Validator.assertEqualLengths(trueLabels, predictedLabels);

        ConfusionMatrix[] confusionMatrices = new ConfusionMatrix[labeledDataset.partitionSize()];
        for (int i = 0; i < confusionMatrices.length; i++) {
            confusionMatrices[i] = ConfusionMatrixCalculator.compute(trueLabels[i], predictedLabels[i]);
        }

        return new SubspatialConfusionMatrix(confusionMatrices);
    }
}
