package utils.linalg;

public class CholeskyDecomposition {
    private org.apache.commons.math3.linear.CholeskyDecomposition decomposition;

    public CholeskyDecomposition(Matrix matrix) {
        this.decomposition = new org.apache.commons.math3.linear.CholeskyDecomposition(matrix.matrix);
    }

    public Matrix getL() {
        return new Matrix(decomposition.getL());
    }
}
