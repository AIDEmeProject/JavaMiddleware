package classifier.kernel;

import data.DataPoint;

import java.util.Collection;

/**
 * A kernel is any function k(x,y) satisfying the Mercer conditions:
 *
 *      - Symmetry: k(x,y) = k(y,x)
 *      - Positivity: for all \( \{x_1, \ldots, x_n\}, \) the matrix \(K_{ij} = k(x_i, x_j\) is positive definite
 */
public interface Kernel {
    /**
     * @param x: a vector
     * @param y: a vector
     * @return the kernel function applied on the input vectors: k(x,y)
     */
    double compute(double[] x, double[] y);

    /**
     * @param x: a data point
     * @param y: a data point
     * @return the kernel function applied on the input data points: k(x,y)
     */
    default double compute(DataPoint x, DataPoint y){
        return compute(x.getData(), y.getData());
    }

    /**
     * @param xs a collection of data points
     * @param y a data point
     * @return computes the vector \([k(x_1, y), ..., k(x_n, y)]\)
     */
    default double[] compute(Collection<? extends DataPoint> xs, DataPoint y){
        double[] kernelVector = new double[xs.size()];

        int i = 0;
        for (DataPoint x : xs) {
            kernelVector[i++] = compute(x.getData(), y.getData());
        }

        return kernelVector;
    }

    /**
     * @param xs a collection of data points
     * @return the kernel matrix \(K_{ij} = k(x_i, x_j)\)
     */
    default double[][] compute(Collection<? extends DataPoint> xs){
        double[][] kernelMatrix = new double[xs.size()][xs.size()];

        int i = 0;
        for (DataPoint x : xs) {
            kernelMatrix[i++] = compute(xs, x);
        }

        return kernelMatrix;
    }
}
