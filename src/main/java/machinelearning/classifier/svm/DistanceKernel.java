package machinelearning.classifier.svm;

import utils.linalg.Matrix;
import utils.linalg.Vector;

public class DistanceKernel extends Kernel {
    @Override
    public double compute(Vector x, Vector y) {
        return x.squaredDistanceTo(y);
    }

    @Override
    public Vector compute(Matrix xs, Vector y) {
        Vector squaredRowNormX = xs.getRowSquaredNorms();
        Vector scalarProduct = xs.multiply(y).iScalarMultiply(-2);
        return scalarProduct.iAdd(squaredRowNormX).iScalarAdd(y.squaredNorm());
    }

    @Override
    public Matrix compute(Matrix xs, Matrix ys) {
        Vector squaredRowNormX = xs.getRowSquaredNorms();
        Vector squaredRowNormY = ys.getRowSquaredNorms();
        Matrix scalarProductMatrix = xs.multiplyTranspose(ys).iScalarMultiply(-2);
        return scalarProductMatrix.iAddColumn(squaredRowNormX).iAddRow(squaredRowNormY);
    }

    @Override
    public Matrix compute(Matrix xs) {
        Vector squaredRowNormX = xs.getRowSquaredNorms();
        Matrix scalarProductMatrix = xs.multiplyTranspose(xs).iScalarMultiply(-2);
        return scalarProductMatrix.iAddColumn(squaredRowNormX).iAddRow(squaredRowNormX);
    }
}
