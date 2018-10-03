package utils.linalg;

import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import utils.Validator;

import java.util.StringJoiner;

import static org.ojalgo.function.PrimitiveFunction.*;

/**
 * A Vector represents a mathematical real euclidean vector. Basically, this module is a wrapper of the Ojalgo's
 * PrimitiveMatrix class. Note that all Vector instances are immutable, i.e. we do not allow modifying a Vector's inner
 * values directly. Consequently, all Vector operations create new Vector instances, leaving the operands untouched.
 */
public class Vector {
    /**
     * A vector object from Apache Commons Math library
     */
    final MatrixStore<Double> vector;

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
            return new Vector(PrimitiveDenseStore.FACTORY.columns(values));
        }

        /**
         * @param dim: dimension of requested vector
         * @return a zeros vector of given dimension
         * @throws IllegalArgumentException if dim is not positive
         */
        public static Vector zeros(int dim) {
            Validator.assertPositive(dim);
            return new Vector(PrimitiveDenseStore.FACTORY.makeZero(dim, 1));
        }

        /**
         * @param vector: a vector
         * @return a zero vector of same dimension as input
         */
        public static Vector zeroslike(Vector vector) {
            return zeros(vector.dim());
        }
    }

    Vector(MatrixStore<Double> vector) {
        this.vector = vector;
    }

    /**
     * @return the number of components of this vector (i.e. its dimension)
     */
    public int dim() {
        return (int) vector.count();
    }

    /**
     * @param index: position to retrieve
     * @return value at position "index"
     * @throws IllegalArgumentException if index is out of bounds (i.e. negative or larger than or equal to the dimension)
     */
    public double get(int index) {
        return vector.get(index);
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
        PrimitiveDenseStore slice = PrimitiveDenseStore.FACTORY.makeZero(to-from, 1);
        vector.logical().offsets(from, -1).limits(to-from, -1).supplyTo(slice);
        return new Vector(slice);
    }

    /**
     * @param other: vector to be added
     * @return the sum of {@code this} and {@code other}
     * @throws IllegalArgumentException if vectors have incompatible dimensions
     */
    public Vector add(Vector other) {
        Validator.assertEquals(dim(), other.dim());
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(dim(), 1);
        result.fillMatching(vector, ADD, other.vector);
        return new Vector(result);
    }

    /**
     * @param other: vector to be subtracted from {@code this}
     * @return the result of the subtraction of {@code this} and {@code other}
     * @throws IllegalArgumentException if vectors have incompatible dimensions
     */
    public Vector subtract(Vector other) {
        Validator.assertEquals(dim(), other.dim());
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(dim(), 1);
        result.fillMatching(vector, SUBTRACT, other.vector);
        return new Vector(result);
    }

    /**
     * @param value: value to multiply each component of {@code this}
     * @return a vector whose every component equals the multiplication of {@code this} by value
     */
    public Vector scalarMultiply(double value) {
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(dim(), 1);
        result.fillMatching(MULTIPLY.second(value), vector);
        return new Vector(result);
    }

    /**
     * @param value: value to divide each component of {@code this}
     * @return a vector whose every component equals the division of {@code this} by value
     */
    public Vector scalarDivide(double value) {
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(dim(), 1);
        result.fillMatching(DIVIDE.second(value), vector);
        return new Vector(result);
    }

    /**
     * @param other: another vector
     * @return the scalar product of {@code this} and the input vector
     * @throws IllegalArgumentException if vectors have incompatible dimensions
     */
    public double dot(Vector other) {
        Validator.assertEquals(dim(), other.dim());
        return vector.dot(other.vector);
    }

    /**
     * @return the squared norm of this vector
     */
    public double squaredNorm() {
        return vector.dot(vector);
    }

    /**
     * @return the norm of this vector
     */
    public double norm() {
        return vector.norm();
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
        return this.subtract(other).squaredNorm();
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

        switch (Integer.compare(newDim, dim())) {
            case -1:
                return slice(0, newDim);
            case 0:
                return this;
            default:
                PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(newDim, 1);
                vector.supplyTo(result);
                return new Vector(result);
        }
    }

    /**
     * @return a new Vector with the number 1.0 appended to the right of {@code this}
     */
    public Vector addBias() {
        PrimitiveDenseStore result = PrimitiveDenseStore.FACTORY.makeZero(dim()+1, 1);
        vector.logical().above(1D).get().supplyTo(result);
        return new Vector(result);
    }

    /**
     * @param other right hand size of outer-product
     * @return the outer product of {@code this} and the input vector
     */
    public Matrix outerProduct(Vector other) {
        return new Matrix(vector.multiply(other.vector.transpose()));
    }

    /**
     * @return an array copy of {@code this}'s content
     */
    public double[] toArray() {
        return vector.toRawCopy1D();
    }

    /**
     * @param other Vector to compare to {@code this}
     * @param precision maximum difference allowed
     * @return true if vectors have the same dimension and all components of their components are equal up to the given precision
     */
    public boolean equals(Vector other, double precision) {
        if (dim() != other.dim()) return false;
        return vector.subtract(other.vector).isAllSmall(precision / 1E-15);  // value <= 1E-15 * precision
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
