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

import utils.Validator;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * A Vector represents a mathematical real euclidean array. Basically, this module is a wrapper of the Ojalgo's
 * PrimitiveMatrix class. Note that all Vector instances are immutable, i.e. we do not allow modifying a Vector's inner
 * values directly. Consequently, all Vector operations create new Vector instances, leaving the operands untouched.
 */
public class Vector extends Tensor<Vector> {

    /**
     * This is a static factory for vector creation. It provides several utility methods for instantiating vectors.
     */
    public static class FACTORY extends Tensor.FACTORY {

        /**
         * @param values: array of vector values. Input array will be copied by default.
         * @return a Vector built from the input array of values
         */
        public static Vector make(double... values) {
            Validator.assertNotEmpty(values);
            return new Vector(values);
        }

        /**
         * @param dim: dimension of requested vector
         * @return a zeros vector of given dimension
         * @throws IllegalArgumentException if dim is not positive
         */
        public static Vector zeros(int dim) {
            Validator.assertPositive(dim);
            return new Vector(new double[dim]);
        }

        /**
         * @param vector: a vector
         * @return a zero vector of same dimension as input
         */
        public static Vector zeroslike(Vector vector) {
            return zeros(vector.dim());
        }

        public static Vector fill(int dim, double value) {
            double[] values = new double[dim];
            Arrays.fill(values, value);
            return new Vector(values);
        }
    }

    Vector(double[] vector) {
        super(vector, new Shape(vector.length));
    }

    /**
     * @return the number of components of this vector (i.e. its dimension)
     */
    public int dim() {
        return length();
    }

    /**
     * @param index: position to retrieve
     * @return value at position "index"
     * @throws IllegalArgumentException if index is out of bounds (i.e. negative or larger than or equal to the dimension)
     */
    public double get(int index) {
        return super.get(index);
    }

    /**
     * @param index: index to set
     * @param value: new value to set
     * @throws IllegalArgumentException if index is out of bounds
     */
    public void set(int index, double value) {
        super.set(value, index);
    }

    @Override
    public Vector copy() {
        return new Vector(array.clone());
    }

    /**
     * @param from: start index of slice (inclusive)
     * @param to: end index of slice (exclusive)
     * @return a slice of the original vector between the specified "from" and "to" indexes.
     * @throws IllegalArgumentException if either "from" or "to" are out of bounds, or "from" is not smaller than "to"
     */
    public Vector slice(int from, int to) {
        if (from < 0 || to > dim()) {
            throw new IllegalArgumentException();
        }

        double[] result = new double[to - from];
        System.arraycopy(array, from, result, 0, result.length);
        return new Vector(result);
    }

    public Vector select(int... indices) {
        Validator.assertNotEmpty(indices);
        double[] selectedFeatures = new double[indices.length];
        for (int i = 0; i < indices.length; i++) {
            selectedFeatures[i] = array[indices[i]];
        }
        return new Vector(selectedFeatures);
    }

    /**
     * @param newNorm norm of output vector
     * @return a Vector parallel to the original one, but of specified norm
     * @throws IllegalArgumentException if newNorm is not positive, or {@code this} is the zero vector
     */
    public Vector normalize(double newNorm) {
        return copy().iNormalize(newNorm);
    }

    /**
     * @param newNorm norm of output vector
     * @return a Vector parallel to the original one, but of specified norm
     * @throws IllegalArgumentException if newNorm is not positive
     * @throws IllegalStateException if {@code this} is the zero vector
     */
    public Vector iNormalize(double newNorm) {
        Validator.assertPositive(newNorm);

        double norm = norm();
        if (norm == 0) {
            throw new IllegalStateException("Cannot normalize zero vector");
        }

        return this.iScalarMultiply(newNorm / norm);
    }

    /**
     * @param newDim dimension of new Vector
     * @return a resized version of {@code this}. If newDim smaller than dim(), {@code this} is truncated. If larger, {@code this}
     * will be padded with zeros to the right.
     * @throws IllegalArgumentException if newDim is not positive
     */
    public Vector resize(int newDim) {
        Validator.assertPositive(newDim);

        if (newDim == dim()) {
            return this;
        }

        double[] result = new double[newDim];
        System.arraycopy(array, 0, result, 0, Math.min(dim(), newDim));
        return new Vector(result);
    }

    /**
     * @return a new Vector with the number 1.0 appended to the right of {@code this}
     */
    public Vector addBias() {
        double[] result = new double[dim() + 1];
        result[0] = 1;
        System.arraycopy(array, 0, result, 1, array.length);
        return new Vector(result);
    }

    /**
     * @param other right hand size of outer-product
     * @return the outer product of {@code this} and the input vector
     */
    public Matrix outerProduct(Vector other) {
        int rows = dim(), cols = other.dim();
        double[] matrix = new double[rows * cols];

        int pos = 0;
        for (int i = 0; i < rows; i++) {
            double value = array[i];
            for (int j = 0; j < cols; j++) {
                matrix[pos++] = value * other.array[j];
            }
        }

        return new Matrix(rows, cols, matrix);
    }

    /**
     * @return the index of the minimum element in this Vector
     */
    public int argmin() {
        int minIndex = 0;
        double minValue = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minIndex = i;
                minValue = array[i];
            }
        }

        return minIndex;
    }

    /**
     * @return an array copy of {@code this}'s content
     */
    public double[] toArray() {
        return Arrays.copyOf(array, dim());
    }

    public boolean hasNaN() {
        for (double x : array) {
            if (Double.isNaN(x)) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < dim(); i++) {
            joiner.add(Double.toString(get(i)));
        }
        return joiner.toString();
    }
}
