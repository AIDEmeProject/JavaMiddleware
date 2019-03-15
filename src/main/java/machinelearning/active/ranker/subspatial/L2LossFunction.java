package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public class L2LossFunction implements LossFunction {

    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return |p_1 - 0.5|^2 + ... + |p_k - 0.5|^2
     */
    @Override
    public Vector apply(Vector[] subspatialProbabilities) {
        Vector score = l2Norm(subspatialProbabilities[0]);

        for (int i = 1; i < subspatialProbabilities.length; i++) {
            score.iAdd(l2Norm(subspatialProbabilities[i]));
        }

        return score;
    }

    private static Vector l2Norm(Vector proba) {
        return proba.iApplyMap(L2LossFunction::l2Norm);
    }

    private static double l2Norm(double p) {
        double diff = p - 0.5;
        return diff * diff;
    }
}
