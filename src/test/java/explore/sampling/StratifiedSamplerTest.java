package explore.sampling;

import data.DataPoint;
import explore.user.DummyUser;
import explore.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StratifiedSamplerTest {

    private StratifiedSampler sampler;
    private Collection<DataPoint> points;
    private User user;

    @BeforeEach
    void setUp() {
        sampler = new StratifiedSampler(2, 2);
        points = new ArrayList<>(4);
        points.add(new DataPoint(0, new double[] {0}));
        points.add(new DataPoint(1, new double[] {1}));
        points.add(new DataPoint(2, new double[] {2}));
        points.add(new DataPoint(3, new double[] {3}));
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
        Set<Long> keys = new HashSet<>();
        keys.add(3L);
        user = new DummyUser(keys);

        assertThrows(RuntimeException.class, () -> sampler.sample(points, user));
    }

    @Test
    void sample_sampleMoreNegativePointsThanPossible_throwsException() {
        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        keys.add(1L);
        keys.add(2L);
        user = new DummyUser(keys);

        assertThrows(RuntimeException.class, () -> sampler.sample(points, user));
    }
}