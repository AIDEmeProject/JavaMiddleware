package explore.sampling;

import data.DataPoint;
import explore.user.User;

import java.util.Collection;
import java.util.List;

/**
 * Interface for the initial sampling procedure. The initial sampling procedure is run before the active learning procedure,
 * with the objective of initially querying the user and attempting to find at least one data point of interest to him.
 *
 * TODO: return LabeledPoints directly?
 */
public interface InitialSampler {
    List<DataPoint> runInitialSample(Collection<DataPoint> unlabeledSet, User user);
}
