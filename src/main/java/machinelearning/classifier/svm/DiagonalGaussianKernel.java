package machinelearning.classifier.svm;

import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class DiagonalGaussianKernel extends Kernel {
    private final Vector diagonal;
    private final GaussianKernel gaussianKernel = new GaussianKernel(1.0);

    public DiagonalGaussianKernel(Vector diagonal) {
        for (int i = 0; i < diagonal.dim(); i++) {
            Validator.assertPositive(diagonal.get(i));
        }

        this.diagonal = diagonal.applyMap(Math::sqrt);
    }

    @Override
    public double compute(Vector x, Vector y) {
        x = x.multiply(this.diagonal);
        y = y.multiply(this.diagonal);
        return gaussianKernel.compute(x, y);
    }

    @Override
    public Vector compute(Matrix xs, Vector y) {
        xs = xs.multiplyRow(this.diagonal);
        y = y.multiply(this.diagonal);
        return gaussianKernel.compute(xs, y);
    }

    @Override
    public Matrix compute(Matrix xs, Matrix ys) {
        return gaussianKernel.compute(xs.multiplyRow(this.diagonal), ys.multiplyRow(this.diagonal));
    }

    @Override
    public Matrix compute(Matrix xs) {
        return gaussianKernel.compute(xs.multiplyRow(this.diagonal));
    }
}
