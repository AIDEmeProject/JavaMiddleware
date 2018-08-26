package machinelearning.active.learning.versionspace.convexbody.sampling;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EllipsoidSamplerTest {
    private int dim;
    private EllipsoidSampler ellipsoidSampler;

    @BeforeEach
    void setUp() {
        dim = 2;
        double[][] matrix = new double[][]{{1, 0}, {0, 4}};
        ellipsoidSampler = new EllipsoidSampler(new Array2DRowRealMatrix(matrix));
    }

    @Test
    void constructor_nonSquareMatrix_throwsException() {
        double[][] matrix = new double[][]{{1, 1, 1}, {2, 2, 2}};
        assertThrows(RuntimeException.class, () -> new EllipsoidSampler(new Array2DRowRealMatrix(matrix)));
    }

    @Test
    void constructor_nonSymmetricMatrix_throwsException() {
        double[][] matrix = new double[][]{{1, 1}, {2, 4}};
        assertThrows(RuntimeException.class, () -> new EllipsoidSampler(new Array2DRowRealMatrix(matrix)));
    }

    @Test
    void constructor_symmetricButNotPositiveDefiniteMatrix_throwsException() {
        double[][] matrix = new double[][]{{-1, 0}, {0, 4}};
        assertThrows(RuntimeException.class, () -> new EllipsoidSampler(new Array2DRowRealMatrix(matrix)));
    }

    @Test
    void sampleDirection_mockedRandomObject_randomCalledDimTimes() {
        Random rand = mock(Random.class);
        when(rand.nextGaussian()).thenReturn(0D);
        ellipsoidSampler.sampleDirection(rand);
        verify(rand, times(dim)).nextGaussian();
    }

    @Test
    void sampleDirection_stubRandomObject_outputEqualsToStubbedValuesMultipliedByCholeskyFactor() {
        Random rand = mock(Random.class);
        when(rand.nextGaussian()).thenReturn(5D, -3D);

        assertArrayEquals(new double[]{5, -6}, ellipsoidSampler.sampleDirection(rand));
    }
}