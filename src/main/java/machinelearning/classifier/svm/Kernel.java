package machinelearning.classifier.svm;

import smile.math.kernel.MercerKernel;
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
        Validator.assertEquals(xs.cols(), y.dim());

        double[] result = new double[xs.rows()];
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
        Validator.assertEquals(xs.cols(), ys.cols());

        double[][] result = new double[xs.rows()][ys.rows()];
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
     * Utility method for getting the Smile's equivalent kernel function
     * @see SvmLearner
     */
    MercerKernel<double[]> getSmileKernel(int dim) {
        throw new RuntimeException("Kernel not supported");
    }
}
