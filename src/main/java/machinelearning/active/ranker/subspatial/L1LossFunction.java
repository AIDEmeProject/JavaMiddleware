package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public class L1LossFunction implements LossFunction {

    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return |p_1 - 0.5| + ... + |p_k - 0.5|
     */
    @Override
    public Vector apply(Vector[] subspatialProbabilities) {
        Vector score = l1Norm(subspatialProbabilities[0]);

        for (int i = 1; i < subspatialProbabilities.length; i++) {
            score.iAdd(l1Norm(subspatialProbabilities[i]));
        }

        return score;
    }

    private static Vector l1Norm(Vector proba) {
        return proba.iApplyMap(L1LossFunction::l1Norm);
    }

    private static double l1Norm(double p) {
        return Math.abs(p - 0.5);
    }
}
