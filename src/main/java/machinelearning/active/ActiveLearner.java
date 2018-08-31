package machinelearning.active;

import data.LabeledPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ActiveLearner represents a classifier adapted to the Active Learning scenario. It is augmented with the capacity of
 * "ranking unlabeled points from least to most informative", retrieving the one it deems most informative for labeling.
 *
 * @author luciano
 */
public abstract class ActiveLearner {
    protected List<LabeledPoint> labeledSet = new ArrayList<>();
    private Ranker ranker;

    protected abstract Ranker computeRanker();

    public Ranker getRanker() {
        return ranker;
    }

    public Ranker update(LabeledPoint labeledPoint){
        labeledSet.add(labeledPoint);
        ranker = computeRanker();
        return ranker;
    }

    public Ranker update(Collection<LabeledPoint> labeledPoints){
        labeledSet.addAll(labeledPoints);
        ranker = computeRanker();
        return ranker;
    }
}
