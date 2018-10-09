package utils.linalg;

import explore.statistics.Statistics;
import utils.Validator;

import java.util.StringJoiner;
import java.util.function.BiFunction;

/**
 * A Matrix represents a mathematical real matrix. Basically, this module is a wrapper of the Ojalgo's
 * PrimitiveMatrix class. Note that all Matrix instances are immutable, i.e. we do not allow modifying its inner
 * values directly. Consequently, all Matrix operations create new Matrix instances, leaving the operands untouched.
 */
public class Matrix {
    /**
     * A matrix object from Apache Commons Math library
     */
    double[] matrix;
    private int rows, cols;

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
            int rows = values.length;
            int cols = values[0].length;

            Validator.assertPositive(rows);
            Validator.assertPositive(cols);

            double[] matrix = new double[rows * cols];

            for (int i = 0; i < rows; i++) {
                if (values[i].length != cols) {
                    throw new RuntimeException();
                }
                System.arraycopy(values[i], 0, matrix, i*cols, cols);
            }

            return new Matrix(rows, cols, matrix);
        }

        /**
         * @param rows: number of rows
         * @param cols: number of columns
         * @param values: values to populate Matrix object, disposed in a row-by-row format
         * @return a Matrix object of specified dimensions and values
         * @throws IllegalArgumentException if either rows or cols are not positive, or values.length is different from rows * cols
         */
        public static Matrix make(int rows, int cols, double... values) {
            Validator.assertPositive(rows);
            Validator.assertPositive(cols);
            Validator.assertEquals(rows * cols, values.length);
            return new Matrix(rows, cols, values);
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
            return new Matrix(rows, cols, new double[rows * cols]);
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
            Validator.assertPositive(dim);
            double[] matrix = new double[dim * dim];
            for (int i = 0; i < dim; i++) {
                matrix[i * (dim+1)] = 1.0;
            }
            return new Matrix(dim, dim, matrix);
        }
    }

    Matrix(int rows, int cols, double[] matrix) {
        this.matrix = matrix;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * @return number of rows
     */
    public int numRows() {
        return rows;
    }

    /**
     * @return number of columns
     */
    public int numCols() {
        return cols;
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
        return matrix[i * cols + j];
    }

    /**
     * @param i row index
     * @return a Vector containing the elements of the i-th row
     * @throws IllegalArgumentException if row index is out of bounds
     */
    public Vector getRow(int i) {
        Validator.assertIndexInBounds(i, 0, numRows());
        double[] row = new double[cols];
        System.arraycopy(matrix, i * cols, row, 0, cols);
        return new Vector(row);
    }

    public Matrix getRows(int... rows) {
        double[] slice = new double[rows.length * cols];
        for (int i = 0; i < rows.length; i++) {
            System.arraycopy(matrix, rows[i] * cols, slice, i * cols, cols);
        }
        return new Matrix(rows.length, cols, slice);
    }

    public Matrix getRowSlice(int from, int to) {
        if (from < 0 || from > to || to > rows) {
            throw new IllegalArgumentException("Indexes " + from + ", " + to + " of bounds for matrix of " + rows + " rows");
        }

        int size = to - from;
        double[] slice = new double[size * cols];
        System.arraycopy(matrix, from * cols, slice, 0, slice.length);
        return new Matrix(size, cols, slice);
    }

    public void swapRows(int i, int j) {
        Validator.assertIndexInBounds(i, 0, rows);
        Validator.assertIndexInBounds(j, 0, rows);

        if (i != j) {
            int offsetI = i * cols, offsetJ = j * cols;
            for (int k = 0; k < cols; k++) {
                double tmp = matrix[offsetI];
                matrix[offsetI++] = matrix[offsetJ];
                matrix[offsetJ++] = tmp;
            }
        }
    }

    public Statistics[] columnStatistics() {
        Statistics[] statistics = new Statistics[cols];

        for (int j = 0; j < cols; j++) {
            statistics[j] = new Statistics("column_" + j, matrix[j]);
        }

        int p = cols;
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                statistics[j].update(matrix[p++]);
            }
        }

        return statistics;
    }

    private static BiFunction<Double, Double, Double> ADD = (x,y) -> x+y;
    private static BiFunction<Double, Double, Double> SUB = (x,y) -> x-y;
    private static BiFunction<Double, Double, Double> MUL = (x,y) -> x*y;
    private static BiFunction<Double, Double, Double> DIV = (x,y) -> x/y;

    private Matrix applyBinaryFunction(Matrix rhs, BiFunction<Double, Double, Double> op) {
        Validator.assertEquals(rows, rhs.rows);
        Validator.assertEquals(cols, rhs.cols);

        double[] result = new double[matrix.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = op.apply(matrix[i], rhs.matrix[i]);
        }

        return new Matrix(rows, cols, result);
    }

    private Matrix applyBinaryFunction(double value, BiFunction<Double, Double, Double> op) {
        double[] result = new double[matrix.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = op.apply(matrix[i], value);
        }

        return new Matrix(rows, cols, result);
    }

    private Matrix applyBinaryFunctionToRows(Vector vector, BiFunction<Double, Double, Double> op) {
        Validator.assertEquals(cols, vector.dim());

        double[] result = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = op.apply(matrix[i], vector.vector[i % cols]);
        }

//        int p = 0;
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                result[p] = op.apply(matrix[p++], vector.vector[j]);
//            }
//        }

        return new Matrix(rows, cols, result);
    }

    /**
     * @param other: matrix to be added
     * @return the sum of {@code this} and {@code other}
     * @throws IllegalArgumentException if matrices have incompatible dimensions
     */
    public Matrix add(Matrix other) {
        return applyBinaryFunction(other, ADD);
    }

    /**
     * @param other: matrix to be subtracted from {@code this}
     * @return the result of the subtraction of {@code this} and {@code other}
     * @throws IllegalArgumentException if matrices have incompatible dimensions
     */
    public Matrix subtract(Matrix other) {
        return applyBinaryFunction(other, SUB);
    }

    public Matrix subtractRow(Vector vector){
        return applyBinaryFunctionToRows(vector, SUB);
    }

    /**
     * @param value: value to multiply each component of {@code this}
     * @return a matrix whose every component equals the multiplication of {@code this} by value
     */
    public Matrix scalarMultiply(double value) {
        return applyBinaryFunction(value, MUL);
    }

    public Matrix divideRow(Vector vector){
        return applyBinaryFunctionToRows(vector, DIV);
    }

    /**
     * @param vector: vector to perform matrix-vector multiplication
     * @return the matrix-vector multiplication of {@code this} and the input vector
     * @throws IllegalArgumentException if the number of columns {@code this} if different from the vector's dimension
     */
    public Vector multiply(Vector vector) {
        if (cols != vector.dim()) {
            throw new IllegalArgumentException();
        }

        int offset = 0;
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[offset + j] * vector.vector[j];
            }
            offset += cols;
            result[i] = sum;
        }

        return new Vector(result);
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
        int size = rows * other.cols;
        double[] values = new double[size];

        for (int p = 0; p < size; p++) {
            int i = (p / other.cols) * cols, j = p % other.cols;
            for (int k = 0; k < cols; k++) {
                values[p] += matrix[i + k] * other.matrix[k * other.cols + j];
            }
        }

        return new Matrix(rows, other.cols, values);
    }

    /**
     * @return the transpose of {@code this}
     */
    public Matrix transpose() {
        double[] transpose = new double[rows * cols];
        for (int p = 0; p < transpose.length; p++) {
            int i = p % rows, j = p / rows;
            transpose[p] = matrix[i * cols + j];
        }
        return new Matrix(cols, rows, transpose);
    }

    public Matrix copy() {
        return new Matrix(rows, cols, matrix.clone());
    }

    /**
     * @return a copy of {@code this} as a double's array
     */
    public double[][] toArray() {
        double[][] array = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = get(i, j);
            }
        }
        return array;
    }

    public boolean equals(Matrix other, double precision) {
        if (numRows() != other.numRows() || numCols() != other.numCols()) return false;

        for (int i = 0; i < matrix.length; i++) {
            if (Math.abs(matrix[i] - other.matrix[i]) > precision) {
                return false;
            }
        }
        return true;
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
