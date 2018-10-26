package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * Linear kernel function. It is defined by the scalar product function:
 *
 *   \( k(x,y) = \langle x, y \rangle \)
 */
public class LinearKernel extends Kernel {
    @Override
    public double compute(Vector x, Vector y) {
        return x.dot(y);
    }

    @Override
    public Vector compute(Matrix xs, Vector y) {
        return xs.multiply(y);
    }

    @Override
    public Matrix compute(Matrix xs, Matrix ys) {
        return xs.multiplyTranspose(ys);
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
