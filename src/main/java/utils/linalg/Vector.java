package utils.linalg;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import utils.Validator;

import java.util.Arrays;

public class Vector {
    private RealVector vector;

    public Vector(int dim) {
        this(new double[dim]);
    }

    public Vector(double... values) {
        this(new ArrayRealVector(values));
    }

    private Vector(RealVector vector) {
        this.vector = vector;
    }

    public int dim() {
        return vector.getDimension();
    }

    public double get(int index) {
        return vector.getEntry(index);
    }

    public Vector add(Vector other) {
        return new Vector(vector.add(other.vector));
    }

    public Vector subtract(Vector other) {
        return new Vector(vector.subtract(other.vector));
    }

    public Vector multiply(double value) {
        return new Vector(vector.mapMultiply(value));
    }

    public double dot(Vector other) {
        return vector.dotProduct(other.vector);
    }

    //TODO: remove this if possible
    public double dot(double[] other) {
        RealVector realVector = new ArrayRealVector(other, false);
        return vector.dotProduct(realVector);
    }

    public double squaredNorm() {
        return vector.dotProduct(vector);
    }

    public double norm() {
        return vector.getNorm();
    }

    public Vector normalize(double newNorm) {
        Validator.assertPositive(newNorm);

        double norm = vector.getNorm();
        if (norm == 0) {
            throw new IllegalStateException("Cannot normalize zero vector");
        }

        return new Vector(vector.mapMultiply(newNorm / norm));
    }

    public double squaredDistanceTo(Vector other) {
        return this.subtract(other).squaredNorm();
    }

    public double distanceTo(Vector other) {
        return vector.getDistance(other.vector);
    }

    public Vector resize(int newDim) {
        Validator.assertPositive(newDim);

        if (newDim == dim()) {
            return this;
        }

        double[] result = new double[newDim];
        System.arraycopy(vector.toArray(), 0, result, 0, newDim);
        return new Vector(result);
    }

    public Vector addBias() {
        RealVector realVector = new ArrayRealVector(vector.getDimension() + 1);
        realVector.setEntry(0, 1.0);
        realVector.setSubVector(1, vector);
        return new Vector(realVector);
    }

    public double[] toArray() {
        return vector.toArray();
    }

    @Override
    public String toString() {
        return Arrays.toString(vector.toArray());
    }
}
