package machinelearning.active.ranker;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import utils.OptimumFinder;
import utils.Validator;

import java.util.Collection;
import java.util.List;

public class MaximumUtilityRanker implements Ranker {
    private final Learner learner;

    /**
     * number of steps to look into the future
     */
    private final int lookahead;

    /**
     * Collection of labeled points
     */
    private final Collection<LabeledPoint> labeledPoints;

    /**
     * maximum utility upper bound calculator
     */
    private final UtilityUpperBoundCalculator calculator;


    public MaximumUtilityRanker(Learner learner, Collection<LabeledPoint> labeledPoints, int lookahead) {
        this.learner = learner;
        this.labeledPoints = labeledPoints;
        this.lookahead = lookahead;
        this.calculator = new UtilityUpperBoundCalculator();
    }

    /**
     * Retrieves the unlabeled point which is expected to return the largest number of positive points in average, in
     * the l-next future steps. If there are less than l unlabeled points remaining, we restrict l = # unlabeled points.
     *
     * @return unlabeled point of highest utility
     */
    @Override
    public DataPoint top(Collection<DataPoint> unlabeledSet) {
        Validator.assertNotEmpty(unlabeledSet);

        int steps = Math.min(unlabeledSet.size(), this.lookahead);

        return utility(unlabeledSet, steps).getOptimizer();
    }

    /**
     * Core function computing the maximum l-steps utility. Refer to [1] and [2] for details.
     * @param unlabeledSet: unlabeled data
     * @param steps: number of steps to look into the future
     * @return optimal utility index and value
     */
    private OptimumFinder.OptimumResult<DataPoint> utility(Collection<DataPoint> unlabeledSet, int steps){
        Classifier classifier = learner.fit(labeledPoints);

        // find the unlabeled point maximizing the probability of being a target
        OptimumFinder.OptimumResult<DataPoint> optimum = OptimumFinder.maximizer(unlabeledSet, classifier::probability);

        // if only one step remain, return previous optimum (i.e. greedy selection)
        if (steps <= 1){
            return optimum;
        }

        calculator.fit(unlabeledSet, classifier, steps);

        // return the unlabeled point maximizing the optimalUtilityGivenPoint.
        // the calculator variable can be used for pruning the tree search provided our classifier implements the computeProbabilityUpperBound method
        // use the previous optimizer point as a "warm-starting" for the tree search
        return OptimumFinder.maximizer(
                unlabeledSet,
                pt -> optimalUtilityGivenPoint(unlabeledSet, steps, pt, classifier.probability(pt)),
                pt -> calculator.upperBound(classifier.probability(pt)),
                optimum.getOptimizer());
    }

    /**
     * Helper function for computing the expected number of labeled points to be retrieved if we start at a particular point.
     * This function if computed recursively, calling the utility() method twice. Refer to [2] for details.
     * @param unlabeledSet: unlabeled data
     * @param steps: number to future steps remaining
     * @param point: row number of starting point
     * @param proba: probability of X[i] being 1
     * @return Expected number of positive points to be retrieved if we start at X[rowNumber]
     */
    private double optimalUtilityGivenPoint(Collection<DataPoint> unlabeledSet, int steps, DataPoint point, double proba){
        // positive label branch
        LabeledPoint newLabeledPoint = new LabeledPoint(point, Label.POSITIVE);
        labeledPoints.add(newLabeledPoint);
        double positiveUtility = utility(unlabeledSet, steps-1).getScore();
        labeledPoints.remove(newLabeledPoint);

        // negative label branch
        newLabeledPoint = new LabeledPoint(point, Label.NEGATIVE);
        labeledPoints.add(newLabeledPoint);
        double negativeUtility = utility(unlabeledSet, steps-1).getScore();
        labeledPoints.remove(newLabeledPoint);

        // return utility
        return (positiveUtility + 1) * proba + negativeUtility * (1 - proba);
    }
}
