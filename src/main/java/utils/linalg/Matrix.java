package utils.linalg;

import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import utils.Validator;

import java.util.StringJoiner;

import static org.ojalgo.function.PrimitiveFunction.*;

/**
 * A Matrix represents a mathematical real matrix. Basically, this module is a wrapper of the Ojalgo's
 * PrimitiveMatrix class. Note that all Matrix instances are immutable, i.e. we do not allow modifying its inner
 * values directly. Consequently, all Matrix operations create new Matrix instances, leaving the operands untouched.
 */
public class Matrix {
    /**
     * A matrix object from Apache Commons Math library
     */
    MatrixStore<Double> matrix;

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
            Validator.assertNotEmpty(values);
            Validator.assertNotEmpty(values[0]);

            int dim = values[0].length;
            for (double[] value : values) {
                if (value.length != dim) {
                    throw new RuntimeException();
                }
            }

            return new Matrix(PrimitiveDenseStore.FACTORY.rows(values));
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

            double[][] matrix = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                System.arraycopy(values, i * cols, matrix[i], 0, cols);
            }

            return make(matrix);
        }

        /**
         * @param rows: number of rows
         * @param cols: number of columns
         * @return a zeros matrix of specified dimension
         * @throws IllegalArgumentException if either rows or cols are not positive
         */
        public static Matrix zeros(int rows, int cols) {
            Validator.assertPositive(rows);
            Validator.assertPositive(cols);
            return new Matrix(PrimitiveDenseStore.FACTORY.makeZero(rows, cols));
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
            return new Matrix(PrimitiveDenseStore.FACTORY.makeEye(dim, dim));
        }
    }

    Matrix(MatrixStore<Double> matrix) {
        this.matrix = matrix;
    }

    /**
     * @return number of rows
     */
    public int numRows() {
        return (int) matrix.countRows();
    }

    /**
     * @return number of columns
     */
    public int numCols() {
        return (int) matrix.countColumns();
    }

    /**
     * @param i: row index
     * @param j: column index
     * @return value at position (i, j)
     * @throws IllegalArgumentException if either i or j is out of bounds
     */
    public double get(int i, int j) {
        Validator.assertIndexInBounds(i, 0, numRows());
        Validator.assertIndexInBounds(j, 0, numCols());
        return matrix.get(i, j);
    }

    /**
     * @param i row index
     * @return a Vector containing the elements of the i-th row
     * @throws IllegalArgumentException if row index is out of bounds
     */
    public Vector getRow(int i) {
        Validator.assertIndexInBounds(i, 0, numRows());
        PrimitiveDenseStore row = PrimitiveDenseStore.FACTORY.makeZero(numCols(), 1);
        matrix.logical().row(i).transpose().supplyTo(row);
        return new Vector(row);
    }

    /**
     * @param other: matrix to be added
     * @return the sum of {@code this} and {@code other}
     * @throws IllegalArgumentException if matrices have incompatible dimensions
     */
    public Matrix add(Matrix other) {
        if (numRows() != other.numRows() || numCols() != other.numCols()) {
            throw new IllegalArgumentException();
        }
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(numRows(), numCols());
        result.fillMatching(matrix, ADD, other.matrix);
        return new Matrix(result);
    }

    /**
     * @param other: matrix to be subtracted from {@code this}
     * @return the result of the subtraction of {@code this} and {@code other}
     * @throws IllegalArgumentException if matrices have incompatible dimensions
     */
    public Matrix subtract(Matrix other) {
        if (numRows() != other.numRows() || numCols() != other.numCols()) {
            throw new IllegalArgumentException();
        }
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(numRows(), numCols());
        result.fillMatching(matrix, SUBTRACT, other.matrix);
        return new Matrix(result);
    }

    /**
     * @param value: value to multiply each component of {@code this}
     * @return a matrix whose every component equals the multiplication of {@code this} by value
     */
    public Matrix scalarMultiply(double value) {
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(numRows(), numCols());
        result.fillMatching(MULTIPLY.second(value), matrix);
        return new Matrix(result);
    }

    /**
     * @param vector: vector to perform matrix-vector multiplication
     * @return the matrix-vector multiplication of {@code this} and the input vector
     * @throws IllegalArgumentException if the number of columns {@code this} if different from the vector's dimension
     */
    public Vector multiply(Vector vector) {
        if (numCols() != vector.dim()) {
            throw new IllegalArgumentException();
        }
        return new Vector(matrix.multiply(vector.vector).transpose());
    }

    /**
     * @param other: right-hand-size of matrix-matrix multiplication
     * @return the result of the matrix-matrix multiplication between {@code this} and {@code other}
     * @throws IllegalArgumentException if the number of columns of {@code this} and the number of rows of {@code other} are distinct
     */
    public Matrix multiply(Matrix other) {
        if (numCols() != other.numRows()) {
            throw new IllegalArgumentException();
        }
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
        return matrix.toRawCopy2D();
    }

    public boolean equals(Matrix other, double precision) {
        if (numRows() != other.numRows() || numCols() != other.numCols()) return false;
        return matrix.subtract(other.matrix).isAllSmall(precision / 1E-15);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.equals((Matrix) o, 1E-15);
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
