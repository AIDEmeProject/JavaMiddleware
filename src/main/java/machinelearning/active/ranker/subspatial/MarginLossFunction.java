package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public class MarginLossFunction implements LossFunction {
    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return |p_1|^2 + ... + |p_k|^2
     */
    @Override
    public Vector apply(Vector[] subspatialProbabilities) {
        Vector score = margin(subspatialProbabilities[0]);

        for (int i = 1; i < subspatialProbabilities.length; i++) {
            score.iAdd(margin(subspatialProbabilities[i]));
        }

        return score;
    }

    private static Vector margin(Vector proba) {
        return proba.iApplyMap(x -> x * x);
    }
}
