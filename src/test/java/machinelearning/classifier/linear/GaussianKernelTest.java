package machinelearning.classifier.linear;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GaussianKernelTest {
    private Kernel kernel;

    @BeforeEach
    void setUp() {
        kernel = new GaussianKernel();
    }

    @Test
    void gammaConstructor_negativeGamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GaussianKernel(-1));
    }


    @Test
    void gammaConstructor_zeroGamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GaussianKernel(0));
    }

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

    @Test
    void compute_gammaEqualsToTwo_returnsExpectedValue() {
        kernel = new GaussianKernel(2);
        DataPoint x, y;
        x = new DataPoint(0, new double[] {1, 2});
        y = new DataPoint(1, new double[] {-2, 3});
        assertEquals(Math.exp(-20), kernel.compute(x, y));
    }

    @Test
    void compute_defaultGamma_returnsExpectedValue() {
        DataPoint x, y;
        x = new DataPoint(0, new double[] {1, 2});
        y = new DataPoint(1, new double[] {-2, 3});
        assertEquals(Math.exp(-5), kernel.compute(x, y));
    }

    @Test
    void computeKernelMatrix_gammaEqualsToTwo_returnsExpectedValue() {
        kernel = new GaussianKernel(2);
        Collection<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, new double[] {1, 2}));
        points.add(new DataPoint(0, new double[] {-2, 3}));
        assertArrayEquals(new double[][] {{1, Math.exp(-20)}, {Math.exp(-20), 1}}, kernel.compute(points));
    }

    @Test
    void computeKernelMatrix_defaultGamma_returnsExpectedValue() {
        Collection<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, new double[] {1, 2}));
        points.add(new DataPoint(0, new double[] {-2, 3}));
        assertArrayEquals(new double[][] {{1, Math.exp(-5)}, {Math.exp(-5), 1}}, kernel.compute(points));
    }
}