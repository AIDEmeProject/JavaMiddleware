package machinelearning.classifier.svm;

import smile.math.kernel.MercerKernel;
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
    MercerKernel<double[]> getSmileKernel(int dim) {
        return new smile.math.kernel.LinearKernel();
    }

    @Override
    public String toString() {
        return "Linear Kernel";
    }
}
