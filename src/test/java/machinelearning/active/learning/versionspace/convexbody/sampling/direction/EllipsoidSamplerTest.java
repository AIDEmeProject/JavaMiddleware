package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EllipsoidSamplerTest {
    private int dim;
    private EllipsoidSampler ellipsoidSampler;

    @BeforeEach
    void setUp() {
        dim = 2;
        double[][] matrix = new double[][]{{1, 0}, {0, 4}};
        ellipsoidSampler = new EllipsoidSampler(Matrix.FACTORY.make(matrix));
    }

    @Test
    void constructor_nonSquareMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 3, 1, 1, 1, 2, 2, 2);
        assertThrows(RuntimeException.class, () -> new EllipsoidSampler(matrix));
    }

    @Test
    void constructor_nonSymmetricMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 1, 1, 2, 4);
        assertThrows(RuntimeException.class, () -> new EllipsoidSampler(matrix));
    }

    @Test
    void constructor_symmetricButNotPositiveDefiniteMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, -1, 0, 0, 4);
        assertThrows(RuntimeException.class, () -> new EllipsoidSampler(matrix));
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

        assertEquals(Vector.FACTORY.make(5, -6), ellipsoidSampler.sampleDirection(rand));
    }
}