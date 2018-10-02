package utils.linalg;

import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;

/**
 * This class computes the Cholesky decomposition of a real, symmetric, positive-definite {@link Matrix}. It is basically
 * a wrapper over Apache Commons Math's CholeskyDecomposition class.
 */
public class CholeskyDecomposition {
    /**
     * The resulting decomposition object from Apache Commons Math library
     */
    private static final Cholesky<Double> decomposition = Cholesky.PRIMITIVE.make();

    /**
     * @param matrix: {@link Matrix} to compute decomposition
     * @throws IllegalArgumentException if matrix is non-square, or not symmetric, or not positive-definite
     */
    public CholeskyDecomposition(Matrix matrix) {
        decomposition.decompose(matrix.matrix);

        if (!decomposition.isSPD()) {
            throw new RuntimeException();
        }
    }

    /**
     * @return Cholesky lower-triangular factorization of input matrix
     */
    public Matrix getL() {
        MatrixStore<Double> L = decomposition.getL();
        return new Matrix(PrimitiveDenseStore.FACTORY.copy(L));  // copy L as PrimitiveDenseStore, which is a more efficient Matrix implementation
    }
}
