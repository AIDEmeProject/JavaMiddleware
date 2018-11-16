package utils.linalg;

import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * This class computes the real Eigenvalue decomposition of a real, symmetric {@link Matrix}. It is basically a wrapper over
 * Apache Commons Math's EigenDecomposition class.
 */
public class EigenvalueDecomposition {
    /**
     * The computed real eigenvectors
     */
    private Vector eigenvalues;

    /**
     * The computed eigenvectors, disposed row-wise
     */
    private Matrix eigenvectors;

    /**
     * @param matrix: {@link Matrix} to compute decomposition
     * @throws IllegalArgumentException if matrix is not square, or does not have a eigenvalue decomposition over the real numbers
     */
    public EigenvalueDecomposition(Matrix matrix) {
        Eigenvalue<Double> decomposition = Eigenvalue.PRIMITIVE.make();  // TODO: use constructor (Matrix, boolean) for symmetric matrices
        decomposition.decompose(PrimitiveDenseStore.FACTORY.rows(matrix.toArray()));

        eigenvalues = getEigenvalues(decomposition);
        eigenvectors = getEigenvectors(decomposition);
    }

    private static Vector getEigenvalues(Eigenvalue<Double> decomposition) {
        Array1D<ComplexNumber> eigenvalues = decomposition.getEigenvalues();

        double[] values = eigenvalues.stream()
                .filter(x -> x.isReal())
                .mapToDouble(ComplexNumber::doubleValue)
                .toArray();

        if (values.length != eigenvalues.length) {
            System.out.println(eigenvalues);
            throw new RuntimeException("Matrix does not have real eigenvalue decomposition.");
        }

        return Vector.FACTORY.make(values);
    }

    private static Matrix getEigenvectors(Eigenvalue<Double> decomposition) {
        MatrixStore<Double> VT = decomposition.getV().transpose();
        return Matrix.FACTORY.make(VT.toRawCopy2D());
    }

    /**
     * @param index position of eigenvalue to retrieve
     * @return the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public double getEigenvalue(int index) {
        return eigenvalues.get(index);
    }

    /**
     * @param index position of eigenvector to retrieve
     * @return the eigenvector associated with the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public Vector getEigenvector(int index) {
        return eigenvectors.getRow(index);
    }
}