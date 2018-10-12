package utils.linalg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VectorTest {
    private Vector vector1, vector2;
    
    @BeforeEach
    void setUp() {
        vector1 = Vector.FACTORY.make(1, 2, 3, 4, 5);
        vector2 = Vector.FACTORY.make(10, 20, 30, 40, 50);
    }

    @Test
    void make_emptyArray_throwsException() {
        assertThrows(RuntimeException.class, Vector.FACTORY::make);
    }

    @Test
    void zeros_zeroDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> Vector.FACTORY.zeros(0));
    }

    @Test
    void zeros_negativeDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> Vector.FACTORY.zeros(-1));
    }

    @Test
    void zeroslike_fiveDimensionalInputVector_returnsFiveDimensionalZeroVector() {
        assertEquals(Vector.FACTORY.zeros(5), Vector.FACTORY.zeroslike(vector1));
    }

    @Test
    void get_negativeIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.get(-1));
    }

    @Test
    void get_indexEqualsDim_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.get(vector1.dim()));
    }

    @Test
    void get_indexFromZeroToDimMinusOne_correctValuesReturned() {
        assertEquals(1, vector1.get(0));
        assertEquals(2, vector1.get(1));
        assertEquals(3, vector1.get(2));
        assertEquals(4, vector1.get(3));
        assertEquals(5, vector1.get(4));
    }

    @Test
    void slice_firstIndexIsNegative_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.slice(-1, 1));
    }

    @Test
    void slice_secondIndexLargerThanDim_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.slice(0, vector1.dim() + 1));
    }

    @Test
    void slice_fistIndexEqualToSecondIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.slice(1, 1));
    }

    @Test
    void slice_getSliceFromZeroToDim_returnsVectorIdenticalToOriginal() {
        assertEquals(vector1, vector1.slice(0, vector1.dim()));
    }

    @Test
    void slice_getSliceFromOneToDimMinusOne_returnsExpectedSlice() {
        assertEquals(Vector.FACTORY.make(2, 3, 4), vector1.slice(1, vector1.dim()-1));
    }

    @Test
    void dim_fiveDimensionalVector_returnsFive() {
        assertEquals(5, vector1.dim());
    }

    /* *************************************
     *              ADDITION
     * ************************************
     */
    @Test
    void iScalarAdd_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iScalarAdd(1));
    }

    @Test
    void iScalarAdd_addZero_resultIdenticalToInputVector() {
        vector1.iScalarAdd(0);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iScalarAdd_addOne_originalVectorHasAllComponentsAddedOne() {
        vector1.iScalarAdd(1);
        assertEquals(Vector.FACTORY.make(2, 3, 4, 5, 6), vector1);
    }

    @Test
    void scalarAdd_addZero_resultIdenticalToInputVector() {
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.scalarAdd(0));
    }

    @Test
    void scalarAdd_addOne_resultVectorHasAllComponentsAddedOne() {
        assertEquals(Vector.FACTORY.make(2, 3, 4, 5, 6), vector1.scalarAdd(1));
    }

    @Test
    void scalarAdd_addOne_originalVectorRemainsUnchanged() {
        vector1.scalarAdd(1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iAdd_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iAdd(vector2));
    }

    @Test
    void iAdd_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.iAdd(vector2));
    }

    @Test
    void iAdd_addZeroVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.zeros(vector1.dim());
        vector1.iAdd(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iAdd_addCompatibleVector_originalVectorHasAllComponentsAdded() {
        vector1.iAdd(vector2);
        assertEquals(Vector.FACTORY.make(11, 22, 33, 44, 55), vector1);
    }

    @Test
    void add_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.add(vector2));
    }

    @Test
    void add_addZeroVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.zeroslike(vector1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.add(vector2));
    }

    @Test
    void add_addNonZeroVector_returnsExpectedResult() {
        assertEquals(Vector.FACTORY.make(11, 22, 33, 44, 55), vector1.add(vector2));
    }

    @Test
    void add_addNonZeroVector_originalVectorRemainsUnchanged() {
        vector1.add(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    /* *************************************
     *              SUBTRACTION
     * ************************************
     */
    @Test
    void iScalarSubtract_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iScalarSubtract(1));
    }

    @Test
    void iScalarSubtract_subtractZero_resultIdenticalToInputVector() {
        vector1.iScalarSubtract(0);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iScalarSubtract_subtractOne_originalVectorHasAllComponentsSubtractedOne() {
        vector1.iScalarSubtract(1);
        assertEquals(Vector.FACTORY.make(0, 1, 2, 3, 4), vector1);
    }

    @Test
    void scalarSubtract_subtractZero_resultIdenticalToInputVector() {
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.scalarSubtract(0));
    }

    @Test
    void scalarSubtract_subtractOne_resultVectorHasAllComponentsSubtractedOne() {
        assertEquals(Vector.FACTORY.make(0, 1, 2, 3, 4), vector1.scalarSubtract(1));
    }

    @Test
    void scalarSubtract_subtractOne_originalVectorRemainsUnchanged() {
        vector1.scalarSubtract(1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iSubtract_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iSubtract(vector2));
    }

    @Test
    void iSubtract_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.iSubtract(vector2));
    }

    @Test
    void iSubtract_subtractZeroVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.zeroslike(vector1);
        vector1.iSubtract(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iSubtract_subtractCompatibleVector_originalVectorHasAllComponentsSubtracted() {
        vector1.iSubtract(vector2);
        assertEquals(Vector.FACTORY.make(-9, -18, -27, -36, -45), vector1);
    }

    @Test
    void subtract_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.subtract(vector2));
    }

    @Test
    void subtract_subtractZeroVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.zeroslike(vector1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.subtract(vector2));
    }

    @Test
    void subtract_subtractNonZeroVector_returnsExpectedResult() {
        assertEquals(Vector.FACTORY.make(-9, -18, -27, -36, -45), vector1.subtract(vector2));
    }

    @Test
    void subtract_subtractNonZeroVector_originalVectorRemainsUnchanged() {
        vector1.subtract(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    /* *************************************
     *            MULTIPLICATION
     * ************************************
     */
    @Test
    void iScalarMultiply_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iScalarMultiply(1));
    }

    @Test
    void iScalarMultiply_multiplyByOne_resultIdenticalToInputVector() {
        vector1.iScalarMultiply(1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iScalarMultiply_multiplyByTwo_originalVectorHasAllComponentsMultipliedByTwo() {
        vector1.iScalarMultiply(2);
        assertEquals(Vector.FACTORY.make(2, 4, 6, 8, 10), vector1);
    }

    @Test
    void scalarMultiply_multiplyByOne_resultIdenticalToInputVector() {
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.scalarMultiply(1));
    }

    @Test
    void scalarMultiply_multiplyByTwo_resultVectorHasAllComponentsMultipliedByTwo() {
        assertEquals(Vector.FACTORY.make(2, 4, 6, 8, 10), vector1.scalarMultiply(2));
    }

    @Test
    void scalarMultiply_multiplyByTwo_originalVectorRemainsUnchanged() {
        vector1.scalarMultiply(2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iMultiply_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iMultiply(vector2));
    }

    @Test
    void iMultiply_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.iMultiply(vector2));
    }

    @Test
    void iMultiply_multiplyByOnesVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.make(1, 1, 1, 1, 1);
        vector1.iMultiply(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iMultiply_multiplyCompatibleVector_originalVectorHasAllComponentsMultiplied() {
        vector1.iMultiply(vector2);
        assertEquals(Vector.FACTORY.make(10, 40, 90, 160, 250), vector1);
    }

    @Test
    void multiply_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.make(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.multiply(vector2));
    }

    @Test
    void multiply_multiplyByOnesVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.make(1, 1, 1, 1, 1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.multiply(vector2));
    }

    @Test
    void multiply_multiplyByNonOnesVector_returnsExpectedResult() {
        assertEquals(Vector.FACTORY.make(10, 40, 90, 160, 250), vector1.multiply(vector2));
    }

    @Test
    void multiply_multiplyByNonOnesVector_originalVectorRemainsUnchanged() {
        vector1.multiply(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    /* *************************************
     *            DIVISION
     * ************************************
     */
    @Test
    void iScalarDivide_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iScalarDivide(1));
    }

    @Test
    void iScalarDivide_divideByOne_resultIdenticalToInputVector() {
        vector1.iScalarDivide(1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iScalarDivide_divideByTwo_originalVectorHasAllComponentsDividedByTwo() {
        vector1.iScalarDivide(2);
        assertEquals(Vector.FACTORY.make(0.5, 1, 1.5, 2, 2.5), vector1);
    }

    @Test
    void divide_divideByOne_resultIdenticalToInputVector() {
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.scalarDivide(1));
    }

    @Test
    void divide_divideByTwo_resultVectorHasAllComponentsDividedByTwo() {
        assertEquals(Vector.FACTORY.make(0.5, 1, 1.5, 2, 2.5), vector1.scalarDivide(2));
    }

    @Test
    void divide_divideByTwo_originalVectorRemainsUnchanged() {
        vector1.scalarDivide(2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iDivide_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(vector1, vector1.iDivide(vector2));
    }

    @Test
    void iDivide_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.iDivide(vector2));
    }

    @Test
    void iDivide_divideByOnesVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.make(1, 1, 1, 1, 1);
        vector1.iDivide(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    @Test
    void iDivide_divideCompatibleVector_originalVectorHasAllComponentsDivided() {
        vector1.iDivide(vector2);
        assertEquals(Vector.FACTORY.make(0.1, 0.1, 0.1, 0.1, 0.1), vector1);
    }

    @Test
    void divide_differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.divide(vector2));
    }

    @Test
    void divide_divideByOnesVector_resultIdenticalToInputVector() {
        vector2 = Vector.FACTORY.make(1, 1, 1, 1, 1);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1.divide(vector2));
    }

    @Test
    void divide_divideByCompatibleVector_returnsExpectedResult() {
        assertEquals(Vector.FACTORY.make(0.1, 0.1, 0.1, 0.1, 0.1), vector1.divide(vector2));
    }

    @Test
    void divide_divideByCompatibleVector_originalVectorRemainsUnchanged() {
        vector1.divide(vector2);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }

    /* *************************************
     *           NORM OPERATIONS
     * *************************************
     */
    @Test
    void dot__differentDimensionVector_throwsException() {
        vector2 = Vector.FACTORY.zeros(vector1.dim() - 1);
        assertThrows(RuntimeException.class, () -> vector1.dot(vector2));
    }

    @Test
    void dot__sameDimensionVector_throwsException() {
        assertEquals(550, vector1.dot(vector2));
    }

    @Test
    void squaredNorm_zeroVector_returnsZero() {
        assertEquals(0, Vector.FACTORY.zeros(2).squaredNorm());
    }

    @Test
    void squaredNorm_nonZeroVector_returnsCorrectValue() {
        assertEquals(1., Vector.FACTORY.make(1, 0).squaredNorm());
        assertEquals(1., Vector.FACTORY.make(0, -1).squaredNorm());
        assertEquals(13., Vector.FACTORY.make(2, 3).squaredNorm());
        assertEquals(13., Vector.FACTORY.make(-2, -3).squaredNorm());
    }

    @Test
    void norm_zeroVector_returnsZero() {
        assertEquals(0, Vector.FACTORY.zeros(2).norm());
    }

    @Test
    void norm_nonZeroVector_returnsCorrectValue() {
        assertEquals(1., Vector.FACTORY.make(1, 0).norm());
        assertEquals(1., Vector.FACTORY.make(0, -1).norm());
        assertEquals(Math.sqrt(2.), Vector.FACTORY.make(1, 1).norm());
        assertEquals(Math.sqrt(2.), Vector.FACTORY.make(-1, -1).norm());
    }

    @Test
    void normalize_zeroVector_throwsException() {
        vector1 = Vector.FACTORY.zeros(2);
        assertThrows(IllegalStateException.class, () -> vector1.normalize(1));
    }

    @Test
    void normalize_zeroNewNorm_throwsException() {
        vector1 = Vector.FACTORY.make(1, 0);
        assertThrows(RuntimeException.class, () -> vector1.normalize(0));
    }

    @Test
    void normalize_negativeNewNorm_throwsException() {
        vector1 = Vector.FACTORY.make(1, 0);
        assertThrows(RuntimeException.class, () -> vector1.normalize(-1));
    }

    @Test
    void normalize_newNormEqualToOne_returnsExpectedVector() {
        assertEquals(Vector.FACTORY.make(1, 0), Vector.FACTORY.make(10, 0).normalize(1));
        assertEquals(Vector.FACTORY.make(0, -1), Vector.FACTORY.make(0, -10).normalize(1));
        assertEquals(Vector.FACTORY.make(-1/Math.sqrt(2), 1/Math.sqrt(2)), Vector.FACTORY.make(-1, 1).normalize(1));
    }

    @Test
    void normalize_newNormEqualToTwo_returnsExpectedVector() {
        assertEquals(Vector.FACTORY.make(2, 0), Vector.FACTORY.make(10, 0).normalize(2));
        assertEquals(Vector.FACTORY.make(0, -2), Vector.FACTORY.make(0, -10).normalize(2));
        assertEquals(Vector.FACTORY.make(-Math.sqrt(2), Math.sqrt(2)), Vector.FACTORY.make(-1, 1).normalize(2));
    }

    @Test
    void squaredDistanceTo_vectorOfDifferentDimensions_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.squaredDistanceTo(Vector.FACTORY.zeros(3)));
    }

    @Test
    void squaredDistanceTo_identicalVectors_returnsZero() {
        assertEquals(0, vector1.squaredDistanceTo(vector1));
    }

    @Test
    void squaredDistanceTo_distinctVectors_returnsExpectedValue() {
        assertEquals(4455, vector1.squaredDistanceTo(vector2));
    }

    @Test
    void distanceTo_vectorOfDifferentDimensions_throwsException() {
        assertThrows(RuntimeException.class, () -> vector1.distanceTo(Vector.FACTORY.zeros(3)));
    }

    @Test
    void distanceTo_identicalVectors_returnsZero() {
        assertEquals(0, vector1.distanceTo(vector1));
    }

    @Test
    void distanceTo_distinctVectors_returnsExpectedValue() {
        assertEquals(Math.sqrt(4455), vector1.distanceTo(vector2), 1e-10);
    }

    /* *************************************
     *           EXTRA OPERATIONS
     * *************************************
     */
    @Test
    void resize_negativeSize_throwsException() {
        int size = -1;
        vector1 = Vector.FACTORY.make(1, 2, 3, 4, 5);
        assertThrows(RuntimeException.class, () -> vector1.resize(size));
    }

    @Test
    void resize_zeroSize_throwsException() {
        int size = 0;
        vector1 = Vector.FACTORY.make(1, 2, 3, 4, 5);
        assertThrows(RuntimeException.class, () -> vector1.resize(size));
    }

    @Test
    void resize_sizeLessThanArraySize_returnsCorrectTruncatedArray() {
        int size = 3;
        vector1 = Vector.FACTORY.make(1, 2, 3, 4, 5);
        assertEquals(Vector.FACTORY.make(1, 2, 3), vector1.resize(size));
    }

    @Test
    void resize_sizeEqualsArraySize_theInputArrayIsReturnedWithoutCopying() {
        int size = 5;
        vector1 = Vector.FACTORY.make(1, 2, 3, 4, 5);
        assertSame(vector1, vector1.resize(size));
    }

    @Test
    void resize_sizeLargerThanArraySize_returnsInputArrayPaddedWithZeros() {
        int size = 7;
        vector1 = Vector.FACTORY.make(1, 2, 3, 4, 5);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5, 0, 0), vector1.resize(size));
    }

    @Test
    void outerProduct_vectorsOfEqualDimensions_returnsCorrectOuterProduct() {
        assertEquals(
                Matrix.FACTORY.make(vector1.dim(), vector2.dim(),
                        10,  20,  30,  40,  50,
                        20,  40,  60,  80, 100,
                        30,  60,  90, 120, 150,
                        40,  80, 120, 160, 200,
                        50, 100, 150, 200, 250),
                vector1.outerProduct(vector2));
    }

    @Test
    void outerProduct_vectorsOfDifferentDimensions_returnsCorrectOuterProduct() {
        vector2 = Vector.FACTORY.make(10, 20, 30);
        assertEquals(
                Matrix.FACTORY.make(vector1.dim(), vector2.dim(),
                        10,  20,  30,
                        20,  40,  60,
                        30,  60,  90,
                        40,  80, 120,
                        50, 100, 150),
                vector1.outerProduct(vector2));
    }


    @Test
    void addBias_anyVector_returnsNewVectorWithAOneAppendedLeft() {
        assertEquals(Vector.FACTORY.make(1, 1, 2, 3, 4, 5), vector1.addBias());
    }

    /* *************************************
     *           UTILITY METHODS
     * *************************************
     */
    @Test
    void toArray_anyVector_returnsArrayOfVectorComponents() {
        assertArrayEquals(new double[] {1, 2, 3, 4, 5}, vector1.toArray());
    }

    @Test
    void toArray_anyVector_changingOutputArrayDoesNotModifyVector() {
        double[] copy = vector1.toArray();
        copy[0] = 1000;
        assertEquals(1, vector1.get(0));
    }

    @Test
    void copy_anyVector_returnsIdenticalVector() {
        assertEquals(vector1, vector1.copy());
    }

    @Test
    void copy_anyVector_changingCloneDoesNotModifyOriginalVector() {
        Vector clone = vector1.copy();
        clone.iScalarAdd(10);
        assertEquals(Vector.FACTORY.make(1, 2, 3, 4, 5), vector1);
    }
}