package classifier.linear;

import utils.Validator;
import utils.linalg.LinearAlgebra;

/**
 * The gaussian kernel is defined as:
 *
 *    \( k(x,y) = \exp \left( -\gamma \Vert x - y \Vert^2 \right) \)
 *
 * where gamma is a positive number. In practice, one usually chooses gamma = 1.0 / num_features.
 */
public class GaussianKernel extends Kernel {
    /**
     * gamma parameter
     */
    private double gamma;

    /**
     * @param gamma gamma parameter of gaussian kernel
     * @throws IllegalArgumentException if gamma is not positive
     */
    public GaussianKernel(double gamma) {
        Validator.assertPositive(gamma);
        this.gamma = gamma;
    }

    /**
     * Uses the default value of 1.0 / num_features for gamma
     */
    public GaussianKernel() {
        this.gamma = 0;
    }

    @Override
    public double compute(double[] x, double[] y) {
        double gamma = this.gamma > 0 ? this.gamma : 1.0 / x.length;
        return Math.exp(-gamma * LinearAlgebra.sqDistance(x, y));
    }
}
