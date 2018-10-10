package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class DistanceKernel extends Kernel {
    @Override
    public double compute(Vector x, Vector y) {
        return x.squaredDistanceTo(y);
    }

//    @Override
//    public Vector compute(Matrix xs, Vector y) {
//        return xs.getRowSquaredNorms().subtract(xs.multiply(y).scalarMultiply(-2)).scalarAdd(y.squaredNorm());
//    }
//
//    @Override
//    public Matrix compute(Matrix xs, Matrix ys) {
//        Vector squaredRowNormX = xs.getRowSquaredNorms();
//        Vector squaredRowNormY = ys.getRowSquaredNorms();
//        Matrix scalarProductMatrix = xs.multiplyTranspose(ys).iScalarMultiply(-2);
//        return scalarProductMatrix.iAddColumn(squaredRowNormX).iAddRow(squaredRowNormY);
//    }
//
//    @Override
//    public Matrix compute(Matrix xs) {
//        Vector squaredRowNormX = xs.getRowSquaredNorms();
//        Matrix scalarProductMatrix = xs.multiplyTranspose(xs).iScalarMultiply(-2);
//        return scalarProductMatrix.iAddColumn(squaredRowNormX).iAddRow(squaredRowNormX);
//    }

    @Override
    void setSvmParameters(svm_parameter parameters) {

    }
}
