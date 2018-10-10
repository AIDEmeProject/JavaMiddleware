package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * A kernel is any function k(x,y) satisfying the Mercer conditions:
 *
 *      - Symmetry: k(x,y) = k(y,x)
 *      - Positivity: for all \( \{x_1, \ldots, x_n\}, \) the matrix \(K_{ij} = k(x_i, x_j\) is positive definite
 */
public abstract class Kernel {
    /**
     * @param x: a vector
     * @param y: a vector
     * @return the kernel function applied on the input vectors: k(x,y)
     */
    public abstract double compute(Vector x, Vector y);

    /**
     * @param xs a collection of data points
     * @param y a data point
     * @return computes the vector \([k(x_1, y), ..., k(x_n, y)]\)
     */
    public Vector compute(Matrix xs, Vector y) {
        Validator.assertEquals(xs.numCols(), y.dim());

        double[] result = new double[xs.numRows()];
        for (int i = 0; i < result.length; i++) {
            result[i] = compute(xs.getRow(i), y);
        }
        return Vector.FACTORY.make(result);
    }

    /**
     * @param xs a collection of data points
     * @param ys a second collection of data points
     * @return computes the matrix \([k(x_i, y_j)]\)
     */
    public Matrix compute(Matrix xs, Matrix ys) {
        Validator.assertEquals(xs.numCols(), ys.numCols());

        double[][] result = new double[xs.numRows()][ys.numRows()];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = compute(xs.getRow(i), ys.getRow(j));
            }
        }
        return Matrix.FACTORY.make(result);
    }

    /**
     * @param xs a collection of data points
     * @return the kernel matrix \(K_{ij} = k(x_i, x_j)\)
     */
    public  Matrix compute(Matrix xs){
        return compute(xs, xs);
    }

    /**
     * Utility method for setting the libsvm's training parameters
     * @see SvmLearner
     */
     abstract void setSvmParameters(svm_parameter parameters);
}
