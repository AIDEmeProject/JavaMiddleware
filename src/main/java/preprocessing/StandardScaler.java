package preprocessing;

import java.util.Arrays;

/**
 * This class is responsible for standardizing each column of a double matrix; in other words, after processing, each
 * column will have mean zero and unit variance. Mean and standard deviation computation is based on Welford's method,
 * also present on Knuth's The Art Of Computer Programming book, Vol 2.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance">Variance computation methods wiki</a>
 */
public class StandardScaler {
    /**
     * Mean of each column
     */
    private double[] mean;

    /**
     * Standard deviation of each column
     */
    private double[] std;

    /**
     * @return whether the fit() method was already called
     */
    public boolean isFit(){
        return mean != null && std != null;
    }

    /**
     * Fit the object to a particular data matrix. Basically, we compute and store the mean and standard deviation of
     * each column of the input matrix.
     * @param X: data matrix to fit
     */
    public void fit(double[][] X){
        validateMatrix(X);

        int dim = X[0].length;
        mean = Arrays.copyOf(X[0], dim);
        std = new double[dim];

        for (int i = 1; i < X.length; i++) {
            for (int j = 0; j < dim; j++) {
                double diff = X[i][j] - mean[j];
                mean[j] += diff / (i+1);
                std[j] += diff * (X[i][j] - mean[j]);
            }
        }

        for (int j = 0; j < dim; j++) {
            if (std[j] == 0){
                throw new IllegalArgumentException("Found a zero-standard deviation column.");
            }

            std[j] = Math.sqrt(std[j] / X.length);
        }
    }

    /**
     * Standardize a given matrix. The input matrix won't change, a new matrix is returned. The fit() method must be
     * called beforehand.
     * @param X: data to standardize
     * @return standardized matrix
     */
    public double[][] transform(double[][] X){
        if (!isFit()){
            throw new RuntimeException("Object was not fit; remember to call fit() before calling transform().");
        }

        validateMatrix(X);

        if (X[0].length != mean.length){
            throw new IllegalArgumentException("Input matrix has a incompatible number of dimensions: expected " + mean.length + " but obtained " + X[0].length);
        }

        double[][] scaled = new double[X.length][X[0].length];

        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[0].length; j++) {
                scaled[i][j] = (X[i][j] - mean[j]) / std[j];
            }
        }

        return scaled;
    }

    private void validateMatrix(double[][] X){
        if (X.length == 0){
            throw new IllegalArgumentException("Received empty data matrix.");
        }

        if (X[0].length == 0){
            throw new IllegalArgumentException("Data points have zero dimension.");
        }
    }
}
