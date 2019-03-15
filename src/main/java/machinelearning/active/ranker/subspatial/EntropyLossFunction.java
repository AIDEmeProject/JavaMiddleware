package machinelearning.active.ranker.subspatial;

import org.apache.commons.math3.util.FastMath;
import utils.linalg.Vector;

public class EntropyLossFunction implements LossFunction {
    private static double LOWER_TOL = 1e-12;
    private static double UPPER_TOL = 1 - LOWER_TOL;

    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return [p_1 * log(p_1) + (1 - p_1) * log(1 - p_1)] + ... + [p_k * log(p_k) + (1 - p_k) * log(1 - p_k)]
     */
    @Override
    public Vector apply(Vector[] subspatialProbabilities) {
        Vector score = entropy(subspatialProbabilities[0]);

        for (int i = 1; i < subspatialProbabilities.length; i++) {
            score.iAdd(entropy(subspatialProbabilities[i]));
        }

        return score;
    }

    private static Vector entropy(Vector p) {
        return p.iApplyMap(EntropyLossFunction::entropy);
    }

    private static double entropy(double x) {
        if (x <= LOWER_TOL || x >= UPPER_TOL)
            return 0D;
        return x * FastMath.log(x) + (1 - x) * FastMath.log(1 - x);
    }
}
