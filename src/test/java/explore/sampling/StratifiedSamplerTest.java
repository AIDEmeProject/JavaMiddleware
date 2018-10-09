package explore.sampling;

import data.IndexedDataset;
import explore.user.User;
import explore.user.UserStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StratifiedSamplerTest {

    private StratifiedSampler sampler;
    private IndexedDataset points;
    private User user;

    @BeforeEach
    void setUp() {
        sampler = new StratifiedSampler(2, 2);

        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0, new double[] {0});
        builder.add(1, new double[] {1});
        builder.add(2, new double[] {2});
        builder.add(3, new double[] {3});

        points = builder.build();
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
    void sample_sampleMorePositivePointsThanPossible_throwsException() {
        Set<Long> keys = new HashSet<>(Collections.singletonList(3L));
        user = new UserStub(keys);

        assertThrows(RuntimeException.class, () -> sampler.runInitialSample(points, user));
    }

    @Test
    void sample_sampleMoreNegativePointsThanPossible_throwsException() {
        Set<Long> keys = new HashSet<>(Arrays.asList(0L, 1L, 2L));
        user = new UserStub(keys);

        assertThrows(RuntimeException.class, () -> sampler.runInitialSample(points, user));
    }
}