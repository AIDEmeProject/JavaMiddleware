package utils.linalg;

import org.apache.commons.math3.linear.EigenDecomposition;

public class EigenvalueDecomposition {
    private EigenDecomposition decomposition;

    public EigenvalueDecomposition(Matrix matrix) {
        this.decomposition = new EigenDecomposition(matrix.matrix);

        if (decomposition.hasComplexEigenvalues()) {
            throw new RuntimeException("Matrix does not have a real eigenvalue decomposition.");
        }
    }

    public double getEigenvalue(int i) {
        return decomposition.getRealEigenvalue(i);
    }

    public Vector getEigenvector(int i) {
        return new Vector(decomposition.getEigenvector(i));
    }
}
