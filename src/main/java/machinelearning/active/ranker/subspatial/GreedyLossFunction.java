package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public class GreedyLossFunction implements LossFunction {
    @Override
    public Vector apply(Vector[] subspatialProbabilities) {
        Vector score = normalize(subspatialProbabilities[0]);

        for (int i = 1; i < subspatialProbabilities.length; i++) {
            score.iMultiply(normalize(subspatialProbabilities[i]));
        }

        return score;
    }

    private static Vector normalize(Vector p) {
        return p.iApplyMap(x -> 1 - 2 * x * (1 - x));
    }
}
