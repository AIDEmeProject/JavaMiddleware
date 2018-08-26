package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import machinelearning.active.learning.versionspace.convexbody.sampling.direction.RandomDirectionSampler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomDirectionSamplerTest {
    private int dim;
    private RandomDirectionSampler randomDirectionSampler;

    @BeforeEach
    void setUp() {
        dim = 2;
        randomDirectionSampler = new RandomDirectionSampler(dim);
    }

    @Test
    void constructor_NegativeDimension_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new RandomDirectionSampler(-1));
    }

    @Test
    void constructor_ZeroDimension_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new RandomDirectionSampler(0));
    }

    @Test
    void sampleDirection_mockedRandomObject_randomCalledDimTimes() {
        Random rand = mock(Random.class);
        when(rand.nextGaussian()).thenReturn(0D);
        randomDirectionSampler.sampleDirection(rand);
        verify(rand, times(dim)).nextGaussian();
    }

    @Test
    void sampleDirection_stubRandomObject_outputEqualsToStubbedValues() {
        Random rand = mock(Random.class);
        when(rand.nextGaussian()).thenReturn(1D, 2D);

        assertArrayEquals(new double[]{1, 2}, randomDirectionSampler.sampleDirection(rand));
    }

    @Test
    void equals_sameDimensionSamplers_returnsTrue() {
        assertEquals(randomDirectionSampler, new RandomDirectionSampler(dim));
    }

    @Test
    void equals_compareWithNull_returnsFalse() {
        assertNotEquals(randomDirectionSampler, null);
    }

    @Test
    void equals_distinctDimensions_returnsFalse() {
        assertNotEquals(randomDirectionSampler, new RandomDirectionSampler(dim+1));
    }
}