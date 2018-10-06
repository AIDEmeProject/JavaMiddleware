package utils.linalg;

import utils.Validator;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.BiFunction;

/**
 * A Vector represents a mathematical real euclidean vector. Basically, this module is a wrapper of the Ojalgo's
 * PrimitiveMatrix class. Note that all Vector instances are immutable, i.e. we do not allow modifying a Vector's inner
 * values directly. Consequently, all Vector operations create new Vector instances, leaving the operands untouched.
 */
public class Vector {
    /**
     * A vector object from Apache Commons Math library
     */
    final double[] vector;

    /**
     * This is a static factory for vector creation. It provides several utility methods for instantiating vectors.
     */
    public static class FACTORY {
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
    }

    Vector(double[] vector) {
        this.vector = vector;
    }

    /**
     * @return the number of components of this vector (i.e. its dimension)
     */
    public int dim() {
        return (int) vector.length;
    }

    /**
     * @param index: position to retrieve
     * @return value at position "index"
     * @throws IllegalArgumentException if index is out of bounds (i.e. negative or larger than or equal to the dimension)
     */
    public double get(int index) {
        return vector[index];
    }

    /**
     * @param from: start index of slice (inclusive)
     * @param to: end index of slice (exclusive)
     * @return a slice of the original vector between the specified "from" and "to" indexes.
     * @throws IllegalArgumentException if either "from" or "to" are out of bounds, or "from" is not smaller than "to"
     */
    public Vector slice(int from, int to) {
        if (from < 0 || from == to || to > dim()) {
            throw new IllegalArgumentException();
        }
        double[] result = new double[to - from];
        System.arraycopy(vector, from, result, 0, result.length);
        return new Vector(result);
    }

    private static BiFunction<Double, Double, Double> ADD = (x,y) -> x+y;
    private static BiFunction<Double, Double, Double> SUB = (x,y) -> x-y;
    private static BiFunction<Double, Double, Double> MUL = (x,y) -> x*y;
    private static BiFunction<Double, Double, Double> DIV = (x,y) -> x/y;

    private Vector applyBinaryFunction(double[] rhs, BiFunction<Double, Double, Double> op) {
        Validator.assertEqualLengths(vector, rhs);

        double[] result = new double[vector.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = op.apply(vector[i], rhs[i]);
        }

        return new Vector(result);
    }

    private Vector applyBinaryFunction(double value, BiFunction<Double, Double, Double> op) {
        double[] result = new double[vector.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = op.apply(vector[i], value);
        }

        return new Vector(result);
    }

    /**
     * @param other: vector to be added
     * @return the sum of {@code this} and {@code other}
     * @throws IllegalArgumentException if vectors have incompatible dimensions
     */
    public Vector add(Vector other) {
        return applyBinaryFunction(other.vector, ADD);
    }

    /**
     * @param other: vector to be subtracted from {@code this}
     * @return the result of the subtraction of {@code this} and {@code other}
     * @throws IllegalArgumentException if vectors have incompatible dimensions
     */
    public Vector subtract(Vector other) {
        return applyBinaryFunction(other.vector, SUB);
    }

    /**
     * @param value: value to multiply each component of {@code this}
     * @return a vector whose every component equals the multiplication of {@code this} by value
     */
    public Vector scalarMultiply(double value) {
        return applyBinaryFunction(value, MUL);
    }

    /**
     * @param value: value to divide each component of {@code this}
     * @return a vector whose every component equals the division of {@code this} by value
     */
    public Vector scalarDivide(double value) {
        return applyBinaryFunction(value, DIV);
    }

    /**
     * @param other: another vector
     * @return the scalar product of {@code this} and the input vector
     * @throws IllegalArgumentException if vectors have incompatible dimensions
     */
    public double dot(Vector other) {
        Validator.assertEquals(dim(), other.dim());
        double sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i] * other.get(i);
        }
        return sum;
    }

    /**
     * @return the squared norm of this vector
     */
    public double squaredNorm() {
        return dot(this);
    }

    /**
     * @return the norm of this vector
     */
    public double norm() {
        return Math.sqrt(squaredNorm());
    }

    /**
     * @param newNorm norm of output vector
     * @return a Vector parallel to the original one, but of specified norm
     * @throws IllegalArgumentException if newNorm is not positive, or {@code this} is the zero vector
     */
    public Vector normalize(double newNorm) {
        Validator.assertPositive(newNorm);

        double norm = norm();
        if (norm == 0) {
            throw new IllegalStateException("Cannot normalize zero vector");
        }

        return scalarMultiply(newNorm / norm);
    }

    /**
     * @param other another vector
     * @return the squared distance between {@code this} and the input vector
     */
    public double squaredDistanceTo(Vector other) {
        Validator.assertEqualLengths(vector, other.vector);

        double sqDistance = 0;
        for (int i = 0; i < dim(); i++) {
            double diff = vector[i] - other.get(i);
            sqDistance += diff * diff;
        }
        return sqDistance;
    }

    /**
     * @param other another vector
     * @return the distance between {@code this} and the input vector
     */
    public double distanceTo(Vector other) {
        return Math.sqrt(squaredDistanceTo(other));
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
        System.arraycopy(vector, 0, result, 0, Math.min(dim(), newDim));
        return new Vector(result);
    }

    /**
     * @return a new Vector with the number 1.0 appended to the right of {@code this}
     */
    public Vector addBias() {
        double[] result = new double[dim() + 1];
        result[0] = 1.0;
        System.arraycopy(vector, 0, result, 1, dim());
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
            for (int j = 0; j < cols; j++) {
                matrix[pos++] = vector[i] * other.get(j);
            }
        }

        return new Matrix(rows, cols, matrix);
    }

    /**
     * @return an array copy of {@code this}'s content
     */
    public double[] toArray() {
        return Arrays.copyOf(vector, dim());
    }

    /**
     * @param other Vector to compare to {@code this}
     * @param precision maximum difference allowed
     * @return true if vectors have the same dimension and all components of their components are equal up to the given precision
     */
    public boolean equals(Vector other, double precision) {
        if (dim() != other.dim()) return false;

        for (int i = 0; i < dim(); i++) {
            if (Math.abs(vector[i] - other.get(i)) > precision) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.equals((Vector) o, 1E-15);
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
