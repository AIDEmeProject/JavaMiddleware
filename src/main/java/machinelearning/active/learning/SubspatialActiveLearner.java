package machinelearning.active.learning;

import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.SubspatialRanker;
import utils.Validator;

public class SubspatialActiveLearner implements ActiveLearner {

    private int[][] columnIndexesPartition;
    private final ActiveLearner[] activeLearners;

    public SubspatialActiveLearner(int[][] columnIndexesPartition, ActiveLearner[] activeLearners) {
        Validator.assertNotEmpty(columnIndexesPartition);
        Validator.assertEqualLengths(columnIndexesPartition, activeLearners);

        this.columnIndexesPartition = columnIndexesPartition;
        this.activeLearners = activeLearners;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        Ranker[] subspaceRankers = new Ranker[activeLearners.length];

        // TODO: run concurrently
        for (int i = 0; i < subspaceRankers.length; i++) {
            subspaceRankers[i] = activeLearners[i].fit(labeledPoints.getPartition(columnIndexesPartition[i], i));
        }

        return new SubspatialRanker(columnIndexesPartition, subspaceRankers);
    }

    @Override
    public void setFactorizationStructure(int[][] partition) {
        columnIndexesPartition = partition;
    }
}
