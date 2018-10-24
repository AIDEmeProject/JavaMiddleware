package explore;

import data.DataPoint;
import data.IndexedDataset;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.active.Ranker;
import utils.RandomState;

import java.util.Collections;
import java.util.List;

/**
 * This module encodes a typical iteration of the Active Learning exploration phase, after the Initial Sampling phase.
 * Here, the next points to label are chosen by the following process:
 *
 *   1) First we must select an unlabeled collection of points to search the most informative point from. With a certain probability,
 *   we search the collection of all Unlabeled Points so far; otherwise, we search only the Unknown collection, i.e. the points
 *   for which our data model is still unsure about. In the first case, the AL algorithm converges faster, while in the second
 *   case our data model suffers the most change.
 *
 *   2) Then, the unlabeled selection of points is further SUB-SAMPLED. This is done to reduce the time-per-iteration.
 *
 *   3) Finally, the Active Learning algorithm runs over the above sub-sample, and a most informative point is retrieved.
 */
class ExploreIteration extends Iteration {
    /**
     * Size of subsample in step 2
     */
    private final int subsampleSize;

    /**
     * Probability of selecting the unknown region for running the AL algorithm.
     */
    private final double searchUnknownRegionProbability;

    public ExploreIteration(ExperimentConfiguration configuration) {
        super(configuration);
        this.subsampleSize = configuration.getSubsampleSize();
        this.searchUnknownRegionProbability = configuration.getTsmConfiguration().getSearchUnknownRegionProbability();
    }

    @Override
    List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, User user, Ranker ranker) {
        IndexedDataset unlabeledData = RandomState.newInstance().nextDouble() <= searchUnknownRegionProbability ? partitionedDataset.getUnknownPoints() : partitionedDataset.getUnlabeledPoints();
        IndexedDataset sample = unlabeledData.sample(subsampleSize);
        return Collections.singletonList(ranker.top(sample));
    }
}