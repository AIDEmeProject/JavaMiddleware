package sampling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservoirSamplerTest {
    @Test
    void sample_negativeLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(-1, 1, i -> false));
    }

    @Test
    void sample_zeroLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(0, 1, i -> false));
    }

    @Test
    void sample_negativeSubsetSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(1, -1, i -> false));
    }

    @Test
    void sample_zeroSubsetSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(1, 0, i -> false));
    }

    @Test
    void sample_subsetSizeLargerThanLength_throwsException() {
        assertThrows(RuntimeException.class, () -> ReservoirSampler.sample(1, 2, i -> false));
    }

    @Test
    void sample_filterIndex0_0IsNeverSampled() {
        assertEquals(1, ReservoirSampler.sample(2, i -> i==0));
    }
}