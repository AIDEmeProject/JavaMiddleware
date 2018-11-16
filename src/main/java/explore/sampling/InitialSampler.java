package explore.sampling;

import data.DataPoint;
import data.IndexedDataset;
import explore.user.User;

import java.util.List;

/**
 * Interface for the initial sampling procedure. The initial sampling procedure is run before the active learning procedure,
 * with the objective of initially querying the user and attempting to find at least one data point of interest.
 */
public interface InitialSampler {
    List<DataPoint> runInitialSample(IndexedDataset unlabeledSet, User user);
}