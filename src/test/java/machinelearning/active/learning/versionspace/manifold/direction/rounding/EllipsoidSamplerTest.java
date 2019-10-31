package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.Manifold;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;

import static org.mockito.Mockito.*;

class EllipsoidSamplerTest {
    private Matrix cholesky;
    private Manifold manifold;
    private Ellipsoid ellipsoid;
    private EllipsoidSampler ellipsoidSampler;

    @BeforeEach
    void setUp() {
        manifold = mock(Manifold.class);
        cholesky = mock(Matrix.class);
        ellipsoid = mock(Ellipsoid.class);
        when(ellipsoid.getCholeskyFactor()).thenReturn(cholesky);

        ellipsoidSampler = new EllipsoidSampler(ellipsoid, manifold);
    }

    @Test
    void constructor_mockedEllipsoid_getCholeskyFactorCalledOnce() {
        verify(ellipsoid).getCholeskyFactor();
    }

    @Test
    void sampleDirection_mockedManifold_sampleVelocityCalledWithInputParameters() {
        Random rand = mock(Random.class);
        Vector point = mock(Vector.class);

        ellipsoidSampler.sampleDirection(point, rand);

        verify(manifold).sampleVelocity(point, rand);
    }

    @Test
    void sampleDirection_mockedCholeskyMatrix_multiplyCalledOnce() {
        Vector point = mock(Vector.class), sample = mock(Vector.class);
        Random rand = mock(Random.class);
        when(manifold.sampleVelocity(point, rand)).thenReturn(sample);

        ellipsoidSampler.sampleDirection(point, rand);

        verify(cholesky).multiply(sample);
    }
}