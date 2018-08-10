package classifier.kernel;

import data.DataPoint;

import java.util.Collection;

public interface Kernel {
    double compute(double[] x, double[] y);

    default double compute(DataPoint x, DataPoint y){
        return compute(x.getData(), y.getData());
    }

    /**
     * Given y and a collection {x_1, ..., x_n}, computes (k(x_1, y), ..., k(x_n, y))
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
     * Given {x_1, ..., x_n}, computes the kernel matrix K = [k(x_i, x_j)]
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
