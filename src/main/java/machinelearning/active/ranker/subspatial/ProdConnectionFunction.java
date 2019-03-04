package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public class ProdConnectionFunction implements ConnectionFunction {

    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return |p_1 * ... * p_k - 0.5| for each data point
     */
    @Override
    public Vector apply(Vector[] subspatialProbabilities) {
        Vector score = subspatialProbabilities[0];

        for (int i = 1; i < subspatialProbabilities.length; i++) {
            score.iMultiply(subspatialProbabilities[i]);
        }

        return normalizeProbabilityVector(score);
    }

    private static Vector normalizeProbabilityVector(Vector proba) {
        return proba.iScalarSubtract(0.5).iApplyMap(Math::abs);
    }
}
