package utils.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsTest {
    private String name;
    private double value;
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        name = "test";
        value = 1;
        statistics = new Statistics(name, value);
    }

    @Test
    void getters_noUpdates_correctDefaultValues() {
        assertEquals(name, statistics.getName());
        assertEquals(value, statistics.getMean());
        assertEquals(0D, statistics.getVariance(), 1e-10);
        assertEquals(1, statistics.getSampleSize());
    }

    @Test
    void update_singleUpdate_meanAndVarianceAreCorrect() {
        statistics.update(5);
        assertEquals(3, statistics.getMean(), 1e-10);
        assertEquals(8, statistics.getVariance(), 1e-10);
        assertEquals(2, statistics.getSampleSize());
    }

    @Test
    void update_twoUpdates_meanAndVarianceAreCorrect() {
        statistics.update(5);
        statistics.update(-3);
        assertEquals(1, statistics.getMean(), 1e-10);
        assertEquals(16, statistics.getVariance(), 1e-10);
        assertEquals(3, statistics.getSampleSize());
    }
}