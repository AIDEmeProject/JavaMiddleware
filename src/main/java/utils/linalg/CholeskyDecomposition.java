package utils.linalg;

/**
 * This class computes the Cholesky decomposition of a real, symmetric, positive-definite {@link Matrix}. It is basically
 * a wrapper over Apache Commons Math's CholeskyDecomposition class.
 */
public class CholeskyDecomposition {
    /**
     * The resulting decomposition object from Apache Commons Math library
     */
    private org.apache.commons.math3.linear.CholeskyDecomposition decomposition;

    /**
     * @param matrix: {@link Matrix} to compute decomposition
     * @throws IllegalArgumentException if matrix is non-square, or not symmetric, or not positive-definite
     */
    public CholeskyDecomposition(Matrix matrix) {
        this.decomposition = new org.apache.commons.math3.linear.CholeskyDecomposition(matrix.matrix);
    }

    /**
     * @return Cholesky lower-triangular factorization of input matrix
     */
    public Matrix getL() {
        return new Matrix(decomposition.getL());
    }
}
