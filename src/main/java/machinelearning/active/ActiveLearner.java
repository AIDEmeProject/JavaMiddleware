package machinelearning.active;

import data.LabeledPoint;

import java.util.Collection;

/**
 * An ActiveLearner object is responsible for training a {@link Ranker} from labeled data. In can also be thought as a
 * Factory of Ranker objects.
 *
 * @author luciano
 */
public interface ActiveLearner {
    /**
     * @param labeledPoints: labeled data to fit Ranker
     * @return a Ranker trained over the input labeled data
     * @throws IllegalArgumentException if labeledPoints is empty  TODO: return RandomRanker instead ?
     */
    Ranker fit(Collection<LabeledPoint> labeledPoints);
}
