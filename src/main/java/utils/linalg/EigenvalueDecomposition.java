package utils.linalg;

import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * This class computes the real Eigenvalue decomposition of a real, symmetric {@link Matrix}. It is basically a wrapper over
 * Apache Commons Math's EigenDecomposition class.
 */
public class EigenvalueDecomposition {
    /**
     * The resulting decomposition object from Apache Commons Math library
     */
    private Matrix D;
    private Matrix V;
    private Eigenvalue<Double> decomposition = Eigenvalue.PRIMITIVE.make();
    /**
     * @param matrix: {@link Matrix} to compute decomposition
     * @throws IllegalArgumentException if matrix is not square, or does not have a eigenvalue decomposition over the real numbers
     */
    public EigenvalueDecomposition(Matrix matrix) {
        decomposition.decompose(PrimitiveDenseStore.FACTORY.copy(matrix.matrix));

        for (ComplexNumber complexNumber : decomposition.getEigenvalues()) {
            if (!complexNumber.isReal()) {
                throw new RuntimeException("Matrix does not have real decomposition.");
            }
        }

        D = new Matrix(PrimitiveMatrix.FACTORY.copy(decomposition.getD()));  //TODO: get eigenvalues only
        V = new Matrix(PrimitiveMatrix.FACTORY.copy(decomposition.getV()));

    }

    /**
     * @param index position of eigenvalue to retrieve
     * @return the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public double getEigenvalue(int index) {
        return D.get(index, index);
    }

    /**
     * @param index position of eigenvector to retrieve
     * @return the eigenvector associated with the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public Vector getEigenvector(int index) {
        return V.getRow(index);
    }
}
