package utils.linalg;

import org.apache.commons.math3.linear.EigenDecomposition;

/**
 * This class computes the real Eigenvalue decomposition of a real, symmetric {@link Matrix}. It is basically a wrapper over
 * Apache Commons Math's EigenDecomposition class.
 */
public class EigenvalueDecomposition {
    /**
     * The resulting decomposition object from Apache Commons Math library
     */
    private EigenDecomposition decomposition;

    /**
     * @param matrix: {@link Matrix} to compute decomposition
     * @throws IllegalArgumentException if matrix is not square, or does not have a eigenvalue decomposition over the real numbers
     */
    public EigenvalueDecomposition(Matrix matrix) {
        this.decomposition = new EigenDecomposition(matrix.matrix);

        if (decomposition.hasComplexEigenvalues()) {
            throw new IllegalArgumentException("Input matrix does not have real eigenvalue decomposition.");
        }
    }

    /**
     * @param index position of eigenvalue to retrieve
     * @return the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public double getEigenvalue(int index) {
        return decomposition.getRealEigenvalue(index);
    }

    /**
     * @param index position of eigenvector to retrieve
     * @return the eigenvector associated with the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public Vector getEigenvector(int index) {
        return new Vector(decomposition.getEigenvector(index));
    }
}
