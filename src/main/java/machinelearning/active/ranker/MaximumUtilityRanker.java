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
    private final List<LabeledPoint> labeledPoints;

    /**
     * maximum utility upper bound calculator
     */
    private final UtilityUpperBoundCalculator calculator;


    public MaximumUtilityRanker(Learner learner, List<LabeledPoint> labeledPoints, int lookahead) {
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

        LabeledDataset labeledDataset = new LabeledDataset(labeledPoints, unlabeledSet);
        labeledDataset.putOnLabeledSet(labeledPoints);

        return utility(labeledDataset, steps).getOptimizer();
    }

    /**
     * Core function computing the maximum l-steps utility. Refer to [1] and [2] for details.
     * @param data: labeled data
     * @param steps: number of steps to look into the future
     * @return optimal utility index and value
     */
    private OptimumFinder.OptimumResult<DataPoint> utility(LabeledDataset data, int steps){
        Classifier classifier = learner.fit(data.getLabeledPoints());

        // find the unlabeled point maximizing the probability of being a target
        OptimumFinder.OptimumResult<DataPoint> optimum = OptimumFinder.maximizer(data.getUnlabeledPoints(), classifier::probability);

        // if only one step remain, return previous optimum (i.e. greedy selection)
        if (steps <= 1){
            return optimum;
        }

        calculator.fit(data, classifier, steps);

        // return the unlabeled point maximizing the optimalUtilityGivenPoint.
        // the calculator variable can be used for pruning the tree search provided our classifier implements the computeProbabilityUpperBound method
        // use the previous optimizer point as a "warm-starting" for the tree search
        return OptimumFinder.maximizer(
                data.getUnlabeledPoints(),
                pt -> optimalUtilityGivenPoint(data, steps, pt, classifier.probability(pt)),
                pt -> calculator.upperBound(classifier.probability(pt)),
                optimum.getOptimizer());
    }

    /**
     * Helper function for computing the expected number of labeled points to be retrieved if we start at a particular point.
     * This function if computed recursively, calling the utility() method twice. Refer to [2] for details.
     * @param data: labeled data so far
     * @param steps: number to future steps remaining
     * @param point: row number of starting point
     * @param proba: probability of X[i] being 1
     * @return Expected number of positive points to be retrieved if we start at X[rowNumber]
     */
    private double optimalUtilityGivenPoint(LabeledDataset data, int steps, DataPoint point, double proba){
        // positive label branch
        data.putOnLabeledSet(point, Label.POSITIVE);
        double positiveUtility = utility(data, steps-1).getScore();
        data.removeFromLabeledSet(point);

        // negative label branch
        data.putOnLabeledSet(point, Label.NEGATIVE);
        double negativeUtility = utility(data, steps-1).getScore();
        data.removeFromLabeledSet(point);

        // return utility
        return (positiveUtility + 1) * proba + negativeUtility * (1 - proba);
    }
}
