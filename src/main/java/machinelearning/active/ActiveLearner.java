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
    private Ranker ranker = null;

    protected abstract Ranker computeRanker();

    public Ranker getRanker() {
        return ranker;
    }

    public final void clear() {
        labeledSet = new ArrayList<>();
        ranker = null;
    }

    public final Ranker update(LabeledPoint labeledPoint){
        labeledSet.add(labeledPoint);
        ranker = computeRanker();
        return ranker;
    }

    public final Ranker update(Collection<LabeledPoint> labeledPoints){
        if (labeledPoints.isEmpty()) {
            return ranker;
        }

        labeledSet.addAll(labeledPoints);
        ranker = computeRanker();
        return ranker;
    }
}
