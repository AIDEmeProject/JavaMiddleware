package machinelearning.active.ranker;

import data.DataPoint;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import utils.OptimumFinder;

import java.util.Collection;
import java.util.Objects;

public class ProbabilityRanker implements Ranker {
    private Classifier classifier;

    public ProbabilityRanker(Classifier classifier) {
        this.classifier = Objects.requireNonNull(classifier);
    }

    /**
     * @return the point in the collection whose predicted probability is the closest to 0.5
     */
    @Override
    public DataPoint top(Collection<DataPoint> unlabeledSet) {
        return OptimumFinder.minimizer(unlabeledSet, pt -> Math.abs(classifier.probability(pt) - 0.5)).getOptimizer();
    }
}
