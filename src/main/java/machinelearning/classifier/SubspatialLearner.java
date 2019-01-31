package machinelearning.classifier;

import data.LabeledDataset;
import utils.Validator;

public class SubspatialLearner implements Learner {
    private final Learner[] subspaceLearners;

    public SubspatialLearner(Learner[] subspaceLearners) {
        Validator.assertNotEmpty(subspaceLearners);

        this.subspaceLearners = subspaceLearners;
    }

    @Override
    public SubspatialClassifier fit(LabeledDataset labeledPoints) {
        Validator.assertEquals(labeledPoints.partitionSize(), subspaceLearners.length);

        int size = labeledPoints.partitionSize();
        Classifier[] subspaceClassifiers = new Classifier[size];
        LabeledDataset[] partitionedData = labeledPoints.getPartitionedData();

        for (int i = 0; i < size; i++) {
            subspaceClassifiers[i] = subspaceLearners[i].fit(partitionedData[i]);
        }

        return new SubspatialClassifier(labeledPoints.getPartitionIndexes(), subspaceClassifiers);
    }
}
