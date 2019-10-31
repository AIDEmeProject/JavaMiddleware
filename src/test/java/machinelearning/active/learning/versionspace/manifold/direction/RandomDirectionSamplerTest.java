package machinelearning.active.learning.versionspace.manifold.direction;

import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.euclidean.EuclideanSpace;
import machinelearning.active.learning.versionspace.manifold.sphere.UnitSphere;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomDirectionSamplerTest {
    private Manifold manifold;
    private RandomDirectionSampler randomDirectionSampler;

    @BeforeEach
    void setUp() {
        manifold = mock(Manifold.class);
        randomDirectionSampler = new RandomDirectionSampler(manifold);
    }

    @Test
    void constructor_nullManifold_throwsException() {
        assertThrows(NullPointerException.class, () -> new RandomDirectionSampler(null));
    }

    @Test
    void sampleDirection_mockedParameters_sampleVelocityCalledWithInputParameters() {
        Vector point = mock(Vector.class);
        Random rand = mock(Random.class);

        randomDirectionSampler.sampleDirection(point, rand);

        verify(manifold).sampleVelocity(point, rand);
    }

    @Test
    void equals_compareWithNull_returnsFalse() {
        assertNotEquals(randomDirectionSampler, null);
    }

    @Test
    void equals_distinctManifolds_returnsFalse() {
        Manifold m1 = EuclideanSpace.getInstance(), m2 = UnitSphere.getInstance();
        assertNotEquals(new RandomDirectionSampler(m1), new RandomDirectionSampler(m2));
    }

    @Test
    void equals_sameManifolds_returnsTrue() {
        Manifold m1 = EuclideanSpace.getInstance(), m2 = EuclideanSpace.getInstance();
        assertEquals(new RandomDirectionSampler(m1), new RandomDirectionSampler(m2));
    }
}