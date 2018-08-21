package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.linalg.LinearAlgebra;

/**
 * Linear kernel function. It is defined by the scalar product function:
 *
 *   \( k(x,y) = \langle x, y \rangle \)
 */
public class LinearKernel extends Kernel {
    @Override
    public double compute(double[] x, double[] y) {
        return LinearAlgebra.dot(x, y);
    }

    @Override
    void setSvmParameters(svm_parameter parameters) {
        parameters.kernel_type = svm_parameter.LINEAR;
    }

    @Override
    public String toString() {
        return "Linear Kernel";
    }
}
