package sampling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StratifiedSamplerTest {

    private StratifiedSampler sampler;

    @BeforeEach
    void setUp() {
        sampler = new StratifiedSampler(2, 2);
    }

    @Test
    void constructor_lessThanZeroPositiveSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new StratifiedSampler(-1, 1));
    }

    @Test
    void constructor_lessThanZeroNegativeSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new StratifiedSampler(1, -1));
    }

    @Test
    void sample_sampleMorePositivePointsThanContainedInArray_throwsException() {
        assertThrows(RuntimeException.class, () -> sampler.sample(new int[]{0,0,0,1}));
    }

    @Test
    void sample_sampleMoreNegativePointsThanContainedInArray_throwsException() {
        assertThrows(RuntimeException.class, () -> sampler.sample(new int[]{0,1,1,1}));
    }
}