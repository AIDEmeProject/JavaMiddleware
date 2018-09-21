package explore;

import data.DataPoint;
import data.PartitionedDataset;
import explore.sampling.InitialSampler;
import explore.user.User;
import machinelearning.active.Ranker;

import java.util.List;

public class InitialIteration extends Iteration {
    private InitialSampler initialSampler;

    public InitialIteration(ExperimentConfiguration configuration) {
        super(configuration);
        this.initialSampler = configuration.getInitialSampler();
    }

    @Override
    List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, User user, Ranker ranker) {
        return initialSampler.runInitialSample(partitionedDataset.getUnlabeledPoints(), user);
    }
}
