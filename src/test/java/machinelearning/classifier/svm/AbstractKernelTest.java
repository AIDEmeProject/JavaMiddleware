package machinelearning.classifier.svm;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractKernelTest {
    protected Kernel kernel;

    @Test
    void compute_vectorsOfDifferentLengths_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> kernel.compute(new double[1], new double[2]));
    }

    @Test
    void compute_DataPointsOfDifferentLengths_throwsException() {
        DataPoint x, y;
        x = new DataPoint(0, new double[1]);
        y = new DataPoint(1, new double[2]);
        assertThrows(IllegalArgumentException.class, () -> kernel.compute(x, y));
    }

    @Test
    void compute_emptyDataPointCollection_returnsEmptyArray() {
        assertArrayEquals(new double[0], kernel.compute(Collections.EMPTY_LIST, new DataPoint(0, new double[1])));
    }

    @Test
    void compute_emptyDataPointCollection_returnsEmptyMatrix() {
        assertArrayEquals(new double[0][0], kernel.compute(Collections.EMPTY_LIST));
    }

    void assertKernelFunctionIsCorrect(double expected, double[] arr1, double[] arr2) {
        DataPoint x, y;
        x = new DataPoint(0, arr1);
        y = new DataPoint(1, arr2);
        assertEquals(expected, kernel.compute(x, y));
    }

    void assertKernelMatrixIsCorrectlyComputed(double[][] expected, double[][] toCompute) {
        Collection<DataPoint> points = new ArrayList<>(toCompute.length);

        for (double[] array : toCompute) {
            points.add(new DataPoint(0, array));
        }

        assertArrayEquals(expected, kernel.compute(points));
    }
}