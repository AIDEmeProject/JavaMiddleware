package utils.linalg;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import utils.Validator;

import java.util.StringJoiner;

public class Matrix {
    RealMatrix matrix;

    public static class FACTORY {
        public static Matrix make(double[][] values) {
            return new Matrix(new Array2DRowRealMatrix(values));
        }

        public static Matrix make(int rows, int cols, double... values) {
            Validator.assertEquals(rows * cols, values.length);

            RealMatrix matrix = new Array2DRowRealMatrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrix.setEntry(i, j, values[i*cols+j]);
                }
            }

            return new Matrix(matrix);
        }

        public static Matrix zeros(int rows, int cols) {
            return new Matrix(new Array2DRowRealMatrix(rows, cols));
        }

        public static Matrix zeroslike(Matrix matrix) {
            return zeros(matrix.numRows(), matrix.numCols());
        }

        public static Matrix identity(int dim) {
            return new Matrix(MatrixUtils.createRealIdentityMatrix(dim));
        }
    }

    Matrix(RealMatrix matrix) {
        this.matrix = matrix;
    }

    public int numRows() {
        return matrix.getRowDimension();
    }

    public int numCols() {
        return matrix.getColumnDimension();
    }

    public double get(int i, int j) {
        return matrix.getEntry(i, j);
    }

    public Vector getRow(int i) {
        return new Vector(matrix.getRowVector(i));
    }

    public Matrix add(Matrix other) {
        return new Matrix(matrix.add(other.matrix));
    }

    public Matrix subtract(Matrix other) {
        return new Matrix(matrix.subtract(other.matrix));
    }

    public Matrix scalarMultiply(double value) {
        return new Matrix(matrix.scalarMultiply(value));
    }

    public Vector multiply(Vector vector) {
        return new Vector(matrix.operate(vector.vector));
    }

    public Matrix multiply(Matrix other) {
        return new Matrix(matrix.multiply(other.matrix));
    }

    public double[][] toArray() {
        return matrix.getData();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix other = (Matrix) o;

        if (numRows() != other.numRows() || numCols() != other.numCols()) return false;

        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                if (get(i, j) != other.get(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < numRows(); i++) {
            joiner.add(getRow(i).toString());
        }
        return joiner.toString();
    }
}
