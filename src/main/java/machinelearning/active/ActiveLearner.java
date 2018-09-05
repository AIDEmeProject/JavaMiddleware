package machinelearning.active;

import data.LabeledPoint;

import java.util.Collection;

/**
 * ActiveLearner represents a classifier adapted to the Active Learning scenario. It is augmented with the capacity of
 * "ranking unlabeled points from least to most informative", retrieving the one it deems most informative for labeling.
 *
 * @author luciano
 */
public interface ActiveLearner {
    Ranker fit(Collection<LabeledPoint> labeledPoints);
}
