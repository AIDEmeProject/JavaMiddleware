package utils.linalg;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface VectorSpace<T extends VectorSpace<T>> {
    BiFunction<Double, Double, Double> ADD = (x, y) -> x + y;
    BiFunction<Double, Double, Double> SUB = (x, y) -> x - y;
    BiFunction<Double, Double, Double> MUL = (x, y) -> x * y;
    BiFunction<Double, Double, Double> DIV = (x, y) -> x / y;

    /* **********************
     *      OPERATIONS
     ************************/
    T applyBinaryFunctionInplace(T rhs, BiFunction<Double, Double, Double> op);

    T applyBinaryFunctionInplace(double value, BiFunction<Double, Double, Double> op);

    default T applyBinaryFunction(T rhs, BiFunction<Double, Double, Double> op) {
        return copy().applyBinaryFunctionInplace(rhs, op);
    }

    default T applyBinaryFunction(double value, BiFunction<Double, Double, Double> op) {
        return copy().applyBinaryFunctionInplace(value, op);
    }

    T iApplyMap(Function<Double, Double> op);

    T copy();

    /**
     * @param other: tensor to be added
     * @return the sum of {@code this} and {@code other}
     * @throws IllegalArgumentException if tensors have incompatible shapes
     */
    default T add(T other) {
        return applyBinaryFunction(other, ADD);
    }

    /**
     * @param other: tensor to be added in-place to {@code this}
     * @return {@code this}
     * @throws IllegalArgumentException if tensors have incompatible dimensions
     */
    default T iAdd(T other) {
        return applyBinaryFunctionInplace(other, ADD);
    }

    /**
     * @param value: value to add each component of {@code this}
     * @return a tensor whose every component equals the sum of {@code this} and {@code value}
     */
    default T scalarAdd(double value) {
        return applyBinaryFunction(value, ADD);
    }

    /**
     * @param value: value to add each component of {@code this} in-place
     * @return {@code this}
     */
    default T iScalarAdd(double value) {
        return applyBinaryFunctionInplace(value, ADD);
    }

    /**
     * @param other: tensor to subtract from {@code this}
     * @return the subtraction of {@code this} and {@code other}
     * @throws IllegalArgumentException if tensors have incompatible shapes
     */
    default T subtract(T other) {
        return applyBinaryFunction(other, SUB);
    }

    /**
     * @param other: tensor to be subtracted in-place to {@code this}
     * @return {@code this}
     * @throws IllegalArgumentException if tensors have incompatible dimensions
     */
    default T iSubtract(T other) {
        return applyBinaryFunctionInplace(other, SUB);
    }

    /**
     * @param value: value to subtract from each component of {@code this}
     * @return a tensor whose every component equals the subtraction of {@code this} and {@code value}
     */
    default T scalarSubtract(double value) {
        return applyBinaryFunction(value, SUB);
    }

    /**
     * @param value: value to subtract from each component of {@code this} in-place
     * @return {@code this}
     */
    default T iScalarSubtract(double value) {
        return applyBinaryFunctionInplace(value, SUB);
    }

    /**
     * @param other: tensor to multiply with {@code this}
     * @return the multiplication of {@code this} and {@code other}
     * @throws IllegalArgumentException if tensors have incompatible shapes
     */
    default T multiply(T other) {
        return applyBinaryFunction(other, MUL);
    }

    /**
     * @param other: tensor to be multiplied in-place to {@code this}
     * @return {@code this}
     * @throws IllegalArgumentException if tensors have incompatible dimensions
     */
    default T iMultiply(T other) {
        return applyBinaryFunctionInplace(other, MUL);
    }

    /**
     * @param value: value to multiply from each component of {@code this}
     * @return a tensor whose every component equals the multiplication of {@code this} and {@code value}
     */
    default T scalarMultiply(double value) {
        return applyBinaryFunction(value, MUL);
    }

    /**
     * @param value: value to multiply each component of {@code this} in-place
     * @return {@code this}
     */
    default T iScalarMultiply(double value) {
        return applyBinaryFunctionInplace(value, MUL);
    }

    /**
     * @param other: tensor to divide {@code this} with
     * @return the division of {@code this} and {@code other}
     * @throws IllegalArgumentException if tensors have incompatible shapes
     */
    default T divide(T other) {
        return applyBinaryFunction(other, DIV);
    }

    /**
     * @param other: tensor to be divide in-place to {@code this}
     * @return {@code this}
     * @throws IllegalArgumentException if tensors have incompatible dimensions
     */
    default T iDivide(T other) {
        return applyBinaryFunctionInplace(other, DIV);
    }

    /**
     * @param value: value to divide from each component of {@code this}
     * @return a tensor whose every component equals the division of {@code this} and {@code value}
     */
    default T scalarDivide(double value) {
        return applyBinaryFunction(value, DIV);
    }

    /**
     * @param value: value to divide each component of {@code this} in-place
     * @return {@code this}
     */
    default T iScalarDivide(double value) {
        return applyBinaryFunctionInplace(value, DIV);
    }

    default T applyMap(Function<Double, Double> op) {
        return copy().iApplyMap(op);
    }
}
