package explore;

import data.DataPoint;
import data.PartitionedDataset;
import explore.sampling.ReservoirSampler;
import explore.user.User;
import machinelearning.active.Ranker;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class ExploreIteration extends Iteration {
    private final int subsampleSize;
    private final double searchUncertainRegionProbability;

    public ExploreIteration(ExperimentConfiguration configuration) {
        super(configuration);
        this.subsampleSize = configuration.getSubsampleSize();
        this.searchUncertainRegionProbability = configuration.getSearchUncertainRegionProbability();
    }

    @Override
    List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, User user, Ranker ranker) {
        List<DataPoint> unlabeledData = new Random().nextDouble() <= searchUncertainRegionProbability ? partitionedDataset.getUncertainPoints() : partitionedDataset.getUnlabeledPoints();
        Collection<DataPoint> sample = ReservoirSampler.sample(unlabeledData, subsampleSize);
        return Collections.singletonList(ranker.top(sample));
    }
}