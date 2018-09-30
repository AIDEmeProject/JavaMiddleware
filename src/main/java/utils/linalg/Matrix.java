package utils.linalg;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import utils.Validator;

import java.util.StringJoiner;

/**
 * A Matrix represents a mathematical real matrix. Basically, this module is a wrapper of the Apache Commons Math's
 * Array2DRowRealMatrix class. Note that all Matrix instances are immutable, i.e. we do not allow modifying its inner
 * values directly. Consequently, all Matrix operations create new Matrix instances, leaving the operands untouched.
 */
public class Matrix {
    /**
     * A matrix object from Apache Commons Math library
     */
    RealMatrix matrix;

    /**
     * This is a static factory for matrix creation. It provides several utility methods for instantiating matrices.
     */
    public static class FACTORY {
        /**
         * @param values: matrix of double values. Input array will be copied by default.
         * @return a Matrix built from the input array of values
         * @throws IllegalArgumentException if input is empty or any two rows have different lengths
         */
        public static Matrix make(double[][] values) {
            return new Matrix(new Array2DRowRealMatrix(values));
        }

        /**
         * @param rows: number of rows
         * @param cols: number of columns
         * @param values: values to populate Matrix object, disposed in a row-by-row format
         * @return a Matrix object of specified dimensions and values
         * @throws IllegalArgumentException if either rows or cols are not positive, or values.length is different from rows * cols
         */
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

        /**
         * @param rows: number of rows
         * @param cols: number of columns
         * @return a zeros matrix of specified dimension
         * @throws IllegalArgumentException if either rows or cols are not positive
         */
        public static Matrix zeros(int rows, int cols) {
            return new Matrix(new Array2DRowRealMatrix(rows, cols));
        }

        /**
         * @param matrix a matrix
         * @return a zeros matrix of same dimensions as input
         */
        public static Matrix zeroslike(Matrix matrix) {
            return zeros(matrix.numRows(), matrix.numCols());
        }

        /**
         * @param dim: dimension of identity matrix
         * @return a dim x dim identity matrix
         * @throws IllegalArgumentException if dim is not positive
         */
        public static Matrix identity(int dim) {
            return new Matrix(MatrixUtils.createRealIdentityMatrix(dim));
        }
    }

    Matrix(RealMatrix matrix) {
        this.matrix = matrix;
    }

    /**
     * @return number of rows
     */
    public int numRows() {
        return matrix.getRowDimension();
    }

    /**
     * @return number of columns
     */
    public int numCols() {
        return matrix.getColumnDimension();
    }

    /**
     * @param i: row index
     * @param j: column index
     * @return value at position (i, j)
     * @throws IllegalArgumentException if either i or j is out of bounds
     */
    public double get(int i, int j) {
        return matrix.getEntry(i, j);
    }

    /**
     * @param i row index
     * @return a Vector containing the elements of the i-th row
     * @throws IllegalArgumentException if row index is out of bounds
     */
    public Vector getRow(int i) {
        return new Vector(matrix.getRowVector(i));
    }

    /**
     * @param other: matrix to be added
     * @return the sum of {@code this} and {@code other}
     * @throws IllegalArgumentException if matrices have incompatible dimensions
     */
    public Matrix add(Matrix other) {
        return new Matrix(matrix.add(other.matrix));
    }

    /**
     * @param other: matrix to be subtracted from {@code this}
     * @return the result of the subtraction of {@code this} and {@code other}
     * @throws IllegalArgumentException if matrices have incompatible dimensions
     */
    public Matrix subtract(Matrix other) {
        return new Matrix(matrix.subtract(other.matrix));
    }

    /**
     * @param value: value to multiply each component of {@code this}
     * @return a matrix whose every component equals the multiplication of {@code this} by value
     */
    public Matrix scalarMultiply(double value) {
        return new Matrix(matrix.scalarMultiply(value));
    }

    /**
     * @param vector: vector to perform matrix-vector multiplication
     * @return the matrix-vector multiplication of {@code this} and the input vector
     * @throws IllegalArgumentException if the number of columns {@code this} if different from the vector's dimension
     */
    public Vector multiply(Vector vector) {
        return new Vector(matrix.operate(vector.vector));
    }

    /**
     * @param other: right-hand-size of matrix-matrix multiplication
     * @return the result of the matrix-matrix multiplication between {@code this} and {@code other}
     * @throws IllegalArgumentException if the number of columns of {@code this} and the number of rows of {@code other} are distinct
     */
    public Matrix multiply(Matrix other) {
        return new Matrix(matrix.multiply(other.matrix));
    }

    /**
     * @return the transpose of {@code this}
     */
    public Matrix transpose() {
        return new Matrix(matrix.transpose());
    }

    /**
     * @return a copy of {@code this} as a double's array
     */
    public double[][] toArray() {
        return matrix.getData();
    }

    public boolean equals(Matrix other, double precision) {
        if (numRows() != other.numRows() || numCols() != other.numCols()) return false;

        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                if (Math.abs(get(i, j) - other.get(i, j)) > precision) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.equals((Matrix) o, 0);
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
