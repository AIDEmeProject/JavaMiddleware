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
import java.util.function.BiFunction;
import java.util.function.Function;

public class Tensor<T extends Tensor<T>> implements VectorSpace<T> {
    final double[] array;
    final Shape shape;

    public static abstract class FACTORY {
        public static <U extends Tensor<U>> U make(Shape shape, double... values) {
            return new Tensor<U>(values, shape).cast();
        }

        public static <U extends Tensor<U>> U zeros(Shape shape) {
            return new Tensor<U>(new double[shape.getCapacity()], shape).cast();
        }

        public static <U extends Tensor<U>> U zeroslike(Tensor<U> tensor) {
            return zeros(tensor.shape);
        }
    }

    Tensor(double[] array, Shape shape) {
        Validator.assertEquals(array.length, shape.getCapacity());
        this.array = array;
        this.shape = shape;
    }

    /**
     * @return the sum of all values
     */
    public double sum() {
        double sum = 0;
        for (double value : array) {
            sum += value;
        }
        return sum;
    }

    public int length() {
        return array.length;
    }

    public double get(int... indexes) {
        return array[shape.getPosition(indexes)];
    }

    public void set(double value, int... indexes) {
        array[shape.getPosition(indexes)] = value;
    }

    private T cast(){
        return (T) this;
    }

    /* **********************
     *      OPERATIONS
     ************************/
    @Override
    public T applyBinaryFunctionInplace(T rhs, BiFunction<Double, Double, Double> op) {
        for (int i = 0; i < array.length; i++) {
            array[i] = op.apply(array[i], rhs.array[i]);
        }
        return cast();
    }

    @Override
    public T applyBinaryFunctionInplace(double value, BiFunction<Double, Double, Double> op) {
        for (int i = 0; i < array.length; i++) {
            array[i] = op.apply(array[i], value);
        }
        return cast();
    }

    @Override
    public T iApplyMap(Function<Double, Double> op) {
        for (int i = 0; i < array.length; i++) {
            array[i] = op.apply(array[i]);
        }
        return cast();
    }

    @Override
    public T copy() {
        return new Tensor<T>(array.clone(), shape).cast();
    }

    private void assertCompatible(Tensor<T> tensor) {
        Validator.assertEquals(shape, tensor.shape);
    }

    /**
     * @param other: another tensor
     * @return the scalar product of {@code this} and the input tensor
     * @throws IllegalArgumentException if tensors have incompatible dimensions
     */
    public double dot(Tensor<T> other) {
        assertCompatible(other);

        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i] * other.array[i];
        }
        return sum;
    }

    /**
     * @return the squared norm of this tensor
     */
    public double squaredNorm() {
        return dot(this);
    }

    /**
     * @return the norm of this tensor
     */
    public double norm() {
        return Math.sqrt(squaredNorm());
    }

    /**
     * @param other another tensor
     * @return the squared distance between {@code this} and the input tensor
     */
    public double squaredDistanceTo(Tensor<T> other) {
        assertCompatible(other);

        double sqDistance = 0;
        for (int i = 0; i < array.length; i++) {
            double diff = array[i] - other.array[i];
            sqDistance += diff * diff;
        }
        return sqDistance;
    }

    /**
     * @param other another tensor
     * @return the distance between {@code this} and the input tensor
     */
    public double distanceTo(Tensor<T> other) {
        return Math.sqrt(squaredDistanceTo(other));
    }

    /**
     * @param other Tensor to compare to {@code this}
     * @param precision maximum difference allowed
     * @return true if tensors have the same shape and all components differ by less than the given precision
     */
    public boolean equals(Tensor<?> other, double precision) {
        if (!shape.equals(other.shape)) return false;

        for (int i = 0; i < array.length; i++) {
            if (Math.abs(array[i] - other.array[i]) > precision) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tensor<?> tensor = (Tensor<?>) o;
        return equals(tensor, 1E-15);
    }

    @Override
    public String toString() {
        return "Tensor{" +
                "array=" + Arrays.toString(array) +
                ", shape=" + shape +
                '}';
    }
}
