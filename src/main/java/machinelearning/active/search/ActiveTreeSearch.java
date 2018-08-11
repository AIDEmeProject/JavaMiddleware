package machinelearning.active.search;

import machinelearning.active.ActiveLearner;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import utils.OptimumFinder;
import utils.Validator;

/**
 * Active Search is a domain of research very close to Active Learning. They differ by the quantity they try to optimize:
 * AL tries to train an accurate classifier with as few labeled points as possible, while AS optimizes to retrieving as
 * many positive points it can within a fixed budget.
 *
 * ActiveTreeSearch implements an optimized version of the Optimal Bayesian Tree Search algorithm (see [1]). In [1], a
 * Dynamic Programming algorithm is proposed for computing the expected number of positive points to be retrieved l-steps
 * in the future. Although the optimality guarantees, its complexity is O((2 * card(X)) ^ l) for retrieving the most informative
 * point, thus impractical in any reasonable scenario.
 *
 * Thus, se also implement an optimization described in [2]. The optimization consists of pruning the tree search by
 * computing relatively inexpensive bound on the expected number of positive points to be retrieved; allowing for skipping
 * points which do not meet the necessary thresholds.
 *
 * References:
 *  [1]   Garnett, R., Krishnamurthy, Y., Wang, D., Schneider, J., and Mann, R.
 *        Bayesian optimal active search on graphs
 *        In Proceedings of the Ninth Workshop on Mining and Learning with Graphs, 2011
 *
 *  [2]   Garnett, R., Krishnamurthy, Y., Xiong, X., Schneider, J.
 *        Bayesian Optimal Active Search and Surveying
 *        ICML, 2012
 */
public class ActiveTreeSearch extends ActiveLearner {
    /**
     * number of steps to look into the future
     */
    private final int lookahead;

    /**
     * maximum utility upper bound calculator
     */
    private UtilityUpperBoundCalculator calculator;

    /**
     * @param learner k-Nearest-Neighbors classifier
     * @param lookahead: number of steps to look ahead at every iteration. Usually 1 and 2 work fine.
     */
    public ActiveTreeSearch(Learner learner, int lookahead) {
        super(learner);

        Validator.assertPositive(lookahead);
        this.lookahead = lookahead;
        this.calculator = new UtilityUpperBoundCalculator();
    }

    /**
     * Retrieves the unlabeled point which is expected to return the largest number of positive points in average, in
     * the l-next future steps. If there are less than l unlabeled points remaining, we restrict l = # unlabeled points.
     *
     * @param data: labeled data object
     * @return row index of most informative unlabeled point
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        Validator.assertNotEmpty(data.getUnlabeledPoints());

        int steps = Math.min(data.getNumUnlabeledPoints(), this.lookahead);

        return utility(data, steps).getOptimizer();
    }

    /**
     * Core function computing the maximum l-steps utility. Refer to [1] and [2] for details.
     * @param data: labeled data
     * @param steps: number of steps to look into the future
     * @return optimal utility index and value
     */
    private OptimumFinder.OptimumResult<DataPoint> utility(LabeledDataset data, int steps){
        Classifier classifier = learner.fit(data.getLabeledPoints());
        double[] probas = classifier.probability(data.getAllPoints());

        // find the unlabeled point maximizing the probability of being a target
        OptimumFinder.OptimumResult<DataPoint> optimum = OptimumFinder.maximizer(data.getUnlabeledPoints(), pt -> probas[pt.getRow()]);

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
                pt -> optimalUtilityGivenPoint(data, steps, pt, probas[pt.getRow()]),
                pt -> calculator.upperBound(probas[pt.getRow()]),
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
        data.putOnLabeledSet(point, 1);
        double positiveUtility = utility(data, steps-1).getScore();
        data.removeFromLabeledSet(point);

        // negative label branch
        data.putOnLabeledSet(point, 0);
        double negativeUtility = utility(data, steps-1).getScore();
        data.removeFromLabeledSet(point);

        // return utility
        return (positiveUtility + 1) * proba + negativeUtility * (1 - proba);
    }
}
