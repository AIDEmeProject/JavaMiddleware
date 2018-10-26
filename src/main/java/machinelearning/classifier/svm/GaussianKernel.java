package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.Validator;
import utils.linalg.Matrix;
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

    private static final DistanceKernel distanceKernel = new DistanceKernel();

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

    private double getGamma(int dim) {
        return this.gamma > 0 ? this.gamma : (1.0 / dim);
    }

    private double gaussianMap(double distance, double gamma) {
        return Math.exp(-gamma * distance);
    }

    @Override
    public double compute(Vector x, Vector y) {
        final double gamma = getGamma(y.dim());
        return gaussianMap(distanceKernel.compute(x, y), gamma);
    }

    @Override
    public Vector compute(Matrix xs, Vector y) {
        final double gamma = getGamma(y.dim());
        return distanceKernel.compute(xs, y).iApplyMap(x -> gaussianMap(x, gamma));
    }

    @Override
    public Matrix compute(Matrix xs, Matrix ys) {
        final double gamma = getGamma(xs.cols());
        return distanceKernel.compute(xs, ys).iApplyMap(x -> gaussianMap(x, gamma));
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
