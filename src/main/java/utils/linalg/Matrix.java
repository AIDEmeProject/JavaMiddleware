/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package utils.linalg;

import explore.statistics.Statistics;
import utils.Validator;

import java.util.StringJoiner;
import java.util.function.BiFunction;

/**
 * A Matrix represents a mathematical real array. Basically, this module is a wrapper of the Ojalgo's
 * PrimitiveMatrix class. Note that all Matrix instances are immutable, i.e. we do not allow modifying its inner
 * values directly. Consequently, all Matrix operations create new Matrix instances, leaving the operands untouched.
 */
public class Matrix extends Tensor<Matrix> {
    /**
     * This is a static factory for array creation. It provides several utility methods for instantiating matrices.
     */
    public static class FACTORY extends Tensor.FACTORY {
        /**
         * @param values: array of double values. Input array will be copied by default.
         * @return a Matrix built from the input array of values
         * @throws IllegalArgumentException if input is empty or any two rows()() have different lengths
         */
        public static Matrix make(double[][] values) {
            int rows = values.length;
            int cols = values[0].length;

            Validator.assertNonNegative(rows);
            Validator.assertNonNegative(cols);

            double[] array = new double[rows * cols];

            for (int i = 0; i < rows; i++) {
                if (values[i].length != cols) {
                    throw new RuntimeException();
                }
                System.arraycopy(values[i], 0, array, i*cols, cols);
            }

            return new Matrix(rows, cols, array);
        }

        public static Matrix make(Vector[] rows) {
            Validator.assertNotEmpty(rows);

            int nrows = rows.length;
            int ncols = rows[0].dim();
            double[] values = new double[nrows * ncols];

            int p = 0;
            for (Vector row : rows) {
                Validator.assertEquals(ncols, row.dim());

                for (int j = 0; j < ncols; j++) {
                    values[p++] = row.get(j);
                }
            }

            return new Matrix(nrows, ncols, values);
        }

        /**
         * @param rows: number of rows()
         * @param cols: number of columns
         * @param values: values to populate Matrix object, disposed in a row-by-row format
         * @return a Matrix object of specified dimensions and values
         * @throws IllegalArgumentException if either rows()() or cols() are not positive, or values.length is different from rows()() * cols()
         */
        public static Matrix make(int rows, int cols, double... values) {
            return new Matrix(rows, cols, values);
        }

        /**
         * @param rows(): number of rows()
         * @param cols(): number of columns
         * @return a zeros matrix of specified dimension
         * @throws IllegalArgumentException if either rows()() or cols() are not positive
         */
        public static Matrix zeros(int rows, int cols) {
            return new Matrix(rows, cols, new double[rows * cols]);
        }

        /**
         * @param matrix a matrix
         * @return a zeros matrix of same dimensions as input
         */
        public static Matrix zeroslike(Matrix matrix) {
            return zeros(matrix.rows(), matrix.cols());
        }

        /**
         * @param dim: dimension of identity array
         * @return a dim x dim identity array
         * @throws IllegalArgumentException if dim is not positive
         */
        public static Matrix identity(int dim) {
            Validator.assertPositive(dim);
            double[] array = new double[dim * dim];
            for (int i = 0; i < dim; i++) {
                array[i * (dim+1)] = 1.0;
            }
            return new Matrix(dim, dim, array);
        }
    }

    Matrix(int rows, int cols, double[] array) {
        super(array, new Shape(rows, cols));
    }

    @Override
    public Matrix copy() {
        return new Matrix(rows(), cols(), array.clone());
    }

    /**
     * @return number of rows()
     */
    public int rows() {
        return shape.get(0);
    }

    /**
     * @return number of columns
     */
    public int cols() {
        return shape.get(1);
    }

    /**
     * @param i: row index
     * @param j: column index
     * @return value at position (i, j)
     * @throws IllegalArgumentException if either i or j is out of bounds
     */
    public double get(int i, int j) {
        return super.get(i, j);
    }

    /**
     * @param i: row index
     * @param j: column index
     * @param value: value to set at position (i, j)
     * @throws IllegalArgumentException if either i or j is out of bounds
     */
    public void set(int i, int j, double value) {
        super.set(value, i, j);
    }

    /**
     * @param i row index
     * @return a Vector containing the elements of the i-th row
     * @throws IllegalArgumentException if row index is out of bounds
     */
    public Vector getRow(int i) {
        Validator.assertIndexInBounds(i, 0, rows());
        double[] row = new double[cols()];
        System.arraycopy(array, i * cols(), row, 0, cols());
        return new Vector(row);
    }

    public void setRow(int row, double[] values) {
        Validator.assertIndexInBounds(row, 0, rows());
        Validator.assertEquals(cols(), values.length);

        System.arraycopy(values, 0, array, row * cols(), values.length);
    }

    /**
     * @param rows: row indexes to extract from {@code this} matrix
     * @return a new matrix containing the input rows in the provided order
     * @throws ArrayIndexOutOfBoundsException if any row index is out-of-bounds
     */
    public Matrix getRows(int... rows) {
        Validator.assertNotEmpty(rows);

        double[] slice = new double[rows.length * cols()];
        for (int i = 0; i < rows.length; i++) {
            System.arraycopy(array, rows[i] * cols(), slice, i * cols(), cols());
        }
        return new Matrix(rows.length, cols(), slice);
    }

    /**
     * @param from: start index (inclusive)
     * @param to: end index (exclusive)
     * @return a copy of a slice of rows from the original matrix
     * @throws IllegalArgumentException if indexes are out-of-bounds, or {@code from} is not smaller than {@code to}
     */
    public Matrix getRowSlice(int from, int to) {
        if (from < 0 || from >= to || to > rows()) {
            throw new IllegalArgumentException("Invalid indexes " + from + " and " + to + " for matrix of " + rows() + " rows");
        }

        int size = to - from;
        double[] slice = new double[size * cols()];
        System.arraycopy(array, from * cols(), slice, 0, slice.length);
        return new Matrix(size, cols(), slice);
    }

    /**
     * Swap two rows in-place
     * @param i: index of one row to swapped
     * @param j: index of another row to be swapped
     * @throws IllegalArgumentException if indexes are out-of-bounds
     */
    public void swapRows(int i, int j) {
        Validator.assertIndexInBounds(i, 0, rows());
        Validator.assertIndexInBounds(j, 0, rows());

        if (i != j) {
            int offsetI = i * cols(), offsetJ = j * cols();
            for (int k = 0; k < cols(); k++) {
                double tmp = array[offsetI];
                array[offsetI++] = array[offsetJ];
                array[offsetJ++] = tmp;
            }
        }
    }

    /**
     * @param i column index
     * @return a Vector containing the elements of the i-th column
     * @throws IllegalArgumentException if column index is out of bounds
     */
    public Vector getCol(int i) {
        Validator.assertIndexInBounds(i, 0, cols());
        double[] col = new double[rows()];

        for (int j = 0; j < col.length; j++) {
            col[j] = array[i];
            i += cols();
        }

        return new Vector(col);
    }

    /**
     * @param cols: columns to retrieve
     * @return a new matrix containing only the specified columns
     * @throws IllegalArgumentException if column index is out-of-bounds
     */
    public Matrix getCols(int... cols) {
        Validator.assertNotEmpty(cols);

        double[] result = new double[rows() * cols.length];

        int p = 0;
        for (int i = 0; i < rows(); i++) {
            for(int j : cols) {
                result[p++] = get(i, j);
            }
        }

        return new Matrix(rows(), cols.length, result);
    }

    /**
     * @param from: start index (inclusive)
     * @param to: end index (exclusive)
     * @return a copy of a slice of columns from the original matrix
     * @throws IllegalArgumentException if indexes are out-of-bounds, or {@code from} is not smaller than {@code to}
     */
    public Matrix getColSlice(int from, int to) {
        if (from < 0 || from >= to || to > cols()) {
            throw new IllegalArgumentException("Invalid indexes " + from + " and " + to + " for matrix of " + cols() + " columns");
        }

        int size = to - from;
        double[] slice = new double[size * rows()];

        int start = from;
        int pos = 0;
        for (int i = 0; i < rows(); i++) {
            System.arraycopy(array, start, slice, pos, size);
            start += cols();
            pos += size;
        }

        return new Matrix(rows(), size, slice);
    }

    /* DIAGONAL OPERATIONS */
    public Matrix fillDiagonal(double value) {
        int size = Math.min(rows(), cols());
        int step = cols() + 1;

        for (int i = 0; i < size; i++) {
            array[i * step] = value;
        }

        return this;
    }

    public Matrix iAddScalarToDiagonal(double value) {
        if (value !=  0) {
            int size = Math.min(rows(), cols());
            int step = cols() + 1;

            for (int i = 0; i < size; i++) {
                array[i * step] += value;
            }
        }

        return this;
    }
    
    /* ROW-WISE OPERATIONS */
    
    /**
     * @param vector: vector to add to each row of {@code this}
     * @return a new matrix containing the result of the addition of {@code vector} to each row of {@code this}
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix addRow(Vector vector){
        return applyBinaryFunctionToRows(vector, ADD);
    }

    /**
     * @param vector: vector to subtract from each row of {@code this}
     * @return a new matrix containing the result of the subtracting {@code vector} from each row of {@code this}
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix subtractRow(Vector vector){
        return applyBinaryFunctionToRows(vector, SUB);
    }

    /**
     * @param vector: vector to element-wise multiply each row of {@code this} with
     * @return a new matrix containing the result of the element-wise multiplication of {@code vector} with each row of matrix
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix multiplyRow(Vector vector){
        return applyBinaryFunctionToRows(vector, MUL);
    }

    /**
     * @param vector: vector to divide each row of {@code this} by
     * @return a new matrix containing the result of the division of each row of {@code this} by {@code vector}
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix divideRow(Vector vector){
        return applyBinaryFunctionToRows(vector, DIV);
    }

    private Matrix applyBinaryFunctionToRows(Vector vector, BiFunction<Double, Double, Double> op) {
        return copy().applyBinaryFunctionToRowsInplace(vector, op);
    }

    /* INPLACE ROW-WISE OPERATIONS */
    /**
     * @param vector: vector to add to each row of {@code this} inplace
     * @return {@code this}, after addition
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iAddRow(Vector vector){
        return applyBinaryFunctionToRowsInplace(vector, ADD);
    }

    /**
     * @param vector: vector to subtract from each row of {@code this} inplace
     * @return {@code this}, after subtraction
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iSubtractRow(Vector vector){
        return applyBinaryFunctionToRowsInplace(vector, SUB);
    }

    /**
     * @param vector: vector to element-wise multiply each row of {@code this} inplace
     * @return {@code this}, after multiplication
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iMultiplyRow(Vector vector){
        return applyBinaryFunctionToRowsInplace(vector, MUL);
    }

    /**
     * @param vector: vector to element-wise divide each row of {@code this} inplace
     * @return {@code this}, after division
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iDivideRow(Vector vector){
        return applyBinaryFunctionToRowsInplace(vector, DIV);
    }

    private Matrix applyBinaryFunctionToRowsInplace(Vector vector, BiFunction<Double, Double, Double> op) {
        Validator.assertEquals(cols(), vector.dim());

        int p = 0;
        for (int i = 0; i < rows(); i++) {
            for (double value : vector.array) {
                array[p] = op.apply(array[p++], value);
            }
        }
        return this;
    }

    /* COLUMN-WISE OPERATIONS */

    /**
     * @param vector: vector to add to each column of {@code this}
     * @return a new matrix containing the result of the addition of {@code vector} to each column of {@code this}
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix addColumn(Vector vector){
        return applyBinaryFunctionToColumns(vector, ADD);
    }

    /**
     * @param vector: vector to subtract from each column of {@code this}
     * @return a new matrix containing the result of the subtracting {@code vector} from each column of {@code this}
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix subtractColumn(Vector vector){
        return applyBinaryFunctionToColumns(vector, SUB);
    }

    /**
     * @param vector: vector to element-wise multiply each column of {@code this} with
     * @return a new matrix containing the result of the element-wise multiplication of {@code vector} with each column of matrix
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix multiplyColumn(Vector vector){
        return applyBinaryFunctionToColumns(vector, MUL);
    }

    /**
     * @param vector: vector to divide each column of {@code this} by
     * @return a new matrix containing the result of the division of each column of {@code this} by {@code vector}
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix divideColumn(Vector vector){
        return applyBinaryFunctionToColumns(vector, DIV);
    }

    private Matrix applyBinaryFunctionToColumns(Vector vector, BiFunction<Double, Double, Double> op) {
        return copy().applyBinaryFunctionToColumnsInplace(vector, op);
    }

    /**
     * @return the statistics (mean, std, ...) of each column in {@code this} matrix.
     */
    public Statistics[] columnStatistics() {
        Statistics[] statistics = new Statistics[cols()];

        for (int j = 0; j < cols(); j++) {
            statistics[j] = new Statistics("column_" + j, array[j]);
        }

        int p = cols();
        for (int i = 1; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                statistics[j].update(array[p++]);
            }
        }

        return statistics;
    }

    /* INPLACE COLUMN-WISE OPERATIONS */

    /**
     * @param vector: vector to add to each column of {@code this} inplace
     * @return {@code this}, after addition
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iAddColumn(Vector vector){
        return applyBinaryFunctionToColumnsInplace(vector, ADD);
    }

    /**
     * @param vector: vector to subtract from each column of {@code this} inplace
     * @return {@code this}, after subtraction
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iSubtractColumn(Vector vector){
        return applyBinaryFunctionToColumnsInplace(vector, SUB);
    }

    /**
     * @param vector: vector to element-wise multiply each column of {@code this} inplace
     * @return {@code this}, after multiplication
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iMultiplyColumn(Vector vector){
        return applyBinaryFunctionToColumnsInplace(vector, MUL);
    }

    /**
     * @param vector: vector to element-wise divide each column of {@code this} inplace
     * @return {@code this}, after division
     * @throws IllegalArgumentException if vector.dim() is different from matrix.columns()
     */
    public Matrix iDivideColumn(Vector vector){
        return applyBinaryFunctionToColumnsInplace(vector, DIV);
    }

    private Matrix applyBinaryFunctionToColumnsInplace(Vector vector, BiFunction<Double, Double, Double> op) {
        Validator.assertEquals(rows(), vector.dim());

        int p = 0, k = 0;
        for (int i = 0; i < rows(); i++) {
            double value = vector.array[k];
            for (int j = 0; j < cols(); j++) {
                array[p] = op.apply(array[p++], value);
            }
            k++;
        }
        return this;
    }
    
    /* MATRIX-VECTOR AND MATRIX-MATRIX OPERATIONS */

    /**
     * @param vector: vector to perform array-vector multiplication
     * @return the array-vector multiplication of {@code this} and the input vector
     * @throws IllegalArgumentException if the number of columns {@code this} if different from the vector's dimension
     */
    public Vector multiply(Vector vector) {
        if (cols() != vector.dim()) {
            throw new IllegalArgumentException();
        }

        double[] result = new double[rows()];

        int start = 0;
        for (int i = 0; i < rows(); i++) {
            int offset = start;
            double sum = 0;

            for (int j = 0; j < cols(); j++) {
                sum += array[offset++] * vector.array[j];
            }

            start += cols();
            result[i] = sum;
        }

        return new Vector(result);
    }

    /**
     * @param other: matrix to be transpose-multiplied
     * @return {@code this} * {@code other}^T
     * @throws IllegalArgumentException if matrices have different number of columns
     */
    public Matrix multiplyTranspose(Matrix other) {
        Validator.assertEquals(cols(), other.cols());

        double[] result = new double[rows() * other.rows()];

        int offsetResult = 0, startThis = 0;
        for (int i = 0; i < rows(); i++) {
            int offsetOther = 0;

            for (int j = 0; j < other.rows(); j++) {
                int offsetThis = startThis;
                double sum = 0;

                for (int k = 0; k < cols(); k++) {
                    sum += array[offsetThis++] * other.array[offsetOther++];
                }

                result[offsetResult] = sum;
                offsetResult++;
            }

            startThis += cols();
        }

        return new Matrix(rows(), other.rows(), result);
    }

    /**
     * @param other: right-hand-size of array-array multiplication
     * @return the result of the array-array multiplication between {@code this} and {@code other}
     * @throws IllegalArgumentException if the number of columns of {@code this} and the number of rows()() of {@code other} are distinct
     */
    public Matrix matrixMultiply(Matrix other) {
        Validator.assertEquals(cols(), other.rows());

        double[] values = new double[rows() * other.cols()];

        int startThis = 0, startThat = 0;
        for (int i = 0; i < rows(); i++) {
            int offsetThat = 0;
            int offsetThis = startThis;

            for (int j = 0; j < other.rows(); j++) {
                int offsetResult = startThat;
                double value = array[offsetThis];

                for (int k = 0; k < other.cols(); k++) {
                    values[offsetResult++] += value * other.array[offsetThat++];
                }

                offsetThis++;
            }

            startThis += cols();
            startThat += other.cols();
        }

        return new Matrix(rows(), other.cols(), values);
    }

    /* AGGREGATION */

    /**
     * @return the sum of each row
     */
    public Vector getRowSums() {
        double[] sums = new double[rows()];

        int p = 0;
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                sums[i] += array[p++];
            }
        }

        return new Vector(sums);
    }

    /* UTILITY FUNCTIONS */

    /**
     * @return a Vector containing the squared norm of each row in the matrix
     */
    public Vector getRowSquaredNorms() {
        double[] norms = new double[rows()];

        int p = 0;
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                norms[i] += array[p] * array[p++];
            }
        }
        return new Vector(norms);
    }

    /**
     * Resizes {@code this} matrix. Smaller number of rows or cols cause a truncation; for larger values, entries are
     * padded with zeros.
     * @param newRows: desired number of rows
     * @param newCols: desired number of columns
     * @return the current matrix resized
     */
    public Matrix resize(int newRows, int newCols) {
        Validator.assertPositive(newRows);
        Validator.assertPositive(newCols);

        // no change in dimensions
        if (newRows == rows() && newCols == cols())
            return this;

        double[] values = new double[newRows * newCols];
        int start = 0, startNew = 0;
        int step = Math.min(cols(), newCols), loopLimit = Math.min(rows(), newRows);

        for (int i = 0; i < loopLimit; i++) {
            System.arraycopy(array, start, values, startNew, step);
            start += cols();
            startNew += newCols;
        }

        return new Matrix(newRows, newCols, values);
    }

    /**
     * @return the transpose of {@code this}
     */
    public Matrix transpose() {
        double[] transpose = new double[rows() * cols()];
        for (int p = 0; p < transpose.length; p++) {
            int i = p % rows(), j = p / rows();
            transpose[p] = array[i * cols() + j];
        }
        return new Matrix(cols(), rows(), transpose);
    }

    public Matrix addBiasColumn() {
        double[] matrixWithBias = new double[array.length + rows()];
        int newCols = cols() + 1;

        for (int i = 0; i < matrixWithBias.length; i+=newCols) {
            matrixWithBias[i] = 1;  // bias column
        }

        for (int i = 0; i < rows(); i++) {
            System.arraycopy(array, i * cols(), matrixWithBias, i * newCols + 1, cols());
        }

        return new Matrix(rows(), newCols, matrixWithBias);
    }

    /**
     * @return a copy of {@code this} as a double's array
     */
    public double[][] toArray() {
        double[][] array = new double[rows()][cols()];
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                array[i][j] = get(i, j);
            }
        }
        return array;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < rows(); i++) {
            joiner.add(getRow(i).toString());
        }
        return joiner.toString();
    }
}
