package sampling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HitAndRunSamplerTest {
    private HitAndRunSampler sampler;

    @BeforeEach
    void setUp() {
        sampler = new HitAndRunSampler(0, 1);
    }

    @Test
    void constructor_negativeWarmup_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler(-1, 1));
    }

    @Test
    void constructor_zeroThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler(1, 0));
    }

    @Test
    void constructor_negativeThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler(1, -1));
    }

    @Test
    void sample_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampler.sample(null, -1));
    }

    @Test
    void sample_nullConvexBody_throwsException() {
        assertThrows(NullPointerException.class, () -> sampler.sample(null, 1));
    }
}