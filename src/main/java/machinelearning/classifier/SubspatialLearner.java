package machinelearning.classifier;

import data.LabeledDataset;
import utils.Validator;

public class SubspatialLearner implements Learner {
    private int[][] partitionIndexes;
    private final Learner[] subspaceLearners;

    public SubspatialLearner(int[][] partitionIndexes, Learner[] subspaceLearners) {
        Validator.assertNotEmpty(partitionIndexes);
        Validator.assertEqualLengths(partitionIndexes, subspaceLearners);

        this.partitionIndexes = partitionIndexes;
        this.subspaceLearners = subspaceLearners;
    }

    @Override
    public Classifier fit(LabeledDataset labeledPoints) {
        Classifier[] subspaceClassifiers = new Classifier[subspaceLearners.length];

        for (int i = 0; i < subspaceClassifiers.length; i++) {
            subspaceClassifiers[i] = subspaceLearners[i].fit(labeledPoints.getPartition(partitionIndexes[i], i));
        }

        return new SubspatialClassifier(partitionIndexes, subspaceClassifiers);
    }

    @Override
    public void setFactorizationStructure(int[][] partition) {
        this.partitionIndexes = partition;
    }
}
