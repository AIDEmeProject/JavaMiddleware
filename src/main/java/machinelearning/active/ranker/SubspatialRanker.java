package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import utils.Validator;
import utils.linalg.Vector;

public class SubspatialRanker implements Ranker {

    private final int[][] columnIndexesPartition;
    private final Ranker[] subspaceRankers;

    public SubspatialRanker(int[][] columnIndexesPartition, Ranker[] subspaceRankers) {
        Validator.assertEqualLengths(columnIndexesPartition, subspaceRankers);

        this.columnIndexesPartition = columnIndexesPartition;
        this.subspaceRankers = subspaceRankers;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        Vector score = Vector.FACTORY.zeros(unlabeledData.length());

        //Arrays.stream(subspaceRankers).map(ranker -> ranker.score(unlabeledData)).reduce(VectorSpace::iAdd);
        // TODO: run concurrently?
        for (int i = 0; i < subspaceRankers.length; i++) {
            score.iAdd(subspaceRankers[i].score(unlabeledData.getCols(columnIndexesPartition[i])));
        }

        return score;
    }
}
