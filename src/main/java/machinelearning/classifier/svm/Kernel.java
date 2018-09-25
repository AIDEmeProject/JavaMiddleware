package machinelearning.classifier.svm;

import data.DataPoint;
import libsvm.svm_parameter;
import utils.linalg.Vector;

import java.util.Collection;

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
     * @param x: a data point
     * @param y: a data point
     * @return the kernel function applied on the input data points: k(x,y)
     */
    public final double compute(DataPoint x, DataPoint y){
        return compute(x.getData(), y.getData());
    }

    /**
     * @param xs a collection of data points
     * @param y a data point
     * @return computes the vector \([k(x_1, y), ..., k(x_n, y)]\)
     */
    public final double[] compute(Collection<? extends DataPoint> xs, Vector y){
        double[] kernelVector = new double[xs.size()];

        int i = 0;
        for (DataPoint x : xs) {
            kernelVector[i++] = compute(x.getData(), y);
        }

        return kernelVector;
    }

    /**
     * @param xs a collection of data points
     * @param y a data point
     * @return computes the vector \([k(x_1, y), ..., k(x_n, y)]\)
     */
    public final double[] compute(Collection<? extends DataPoint> xs, DataPoint y){
        return compute(xs, y.getData());
    }

    /**
     * @param xs a collection of data points
     * @return the kernel matrix \(K_{ij} = k(x_i, x_j)\)
     */
    public final double[][] compute(Collection<? extends DataPoint> xs){
        double[][] kernelMatrix = new double[xs.size()][xs.size()];

        int i = 0;
        for (DataPoint x : xs) {
            kernelMatrix[i++] = compute(xs, x);
        }

        return kernelMatrix;
    }

    /**
     * Utility method for setting the libsvm's training parameters
     * @see SvmLearner
     */
     abstract void setSvmParameters(svm_parameter parameters);
}
