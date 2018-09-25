package utils.linalg;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import utils.Validator;

import java.util.Arrays;

public class Vector {
    RealVector vector;

    public static class FACTORY {
        public static Vector make(double... values) {
            Validator.assertNotEmpty(values);
            return new Vector(new ArrayRealVector(values));
        }

        public static Vector zeros(int dim) {
            Validator.assertPositive(dim);
            return make(new double[dim]);
        }

        public static Vector zeroslike(Vector vector) {
            return zeros(vector.dim());
        }
    }

    Vector(RealVector vector) {
        this.vector = vector;
    }

    public int dim() {
        return vector.getDimension();
    }

    public double get(int index) {
        try {
            return vector.getEntry(index);
        } catch (OutOfRangeException ex) {
            throw new IndexOutOfBoundsException();
        }
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

        switch (Integer.compare(newDim, dim())) {
            case -1:
                return new Vector(vector.getSubVector(0, newDim));
            case 0:
                return this;
            default:
                return new Vector(vector.append(new ArrayRealVector(newDim - dim())));
        }
    }

    public Matrix outerProduct() {
        return new Matrix(vector.outerProduct(vector));
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

    public boolean equals(Vector other, double precision) {
        if (dim() != other.dim()) return false;
        for (int i = 0; i < dim(); i++) {
            if (Math.abs(vector.getEntry(i) - other.get(i)) > precision) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector1 = (Vector) o;
        return vector.equals(vector1.vector);
    }

    @Override
    public String toString() {
        return vector.toString();
    }
}
