package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.Validator;
import utils.linalg.Vector;


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
    public double compute(Vector x, Vector y) {
        double gamma = this.gamma > 0 ? this.gamma : 1.0 / x.dim();
        return Math.exp(-gamma * x.squaredDistanceTo(y));
    }

    @Override
    void setSvmParameters(svm_parameter parameters) {
        parameters.kernel_type = svm_parameter.RBF;
        parameters.gamma = gamma;
    }

    @Override
    public String toString() {
        return "Gaussian Kernel gamma=" + gamma;
    }
}
