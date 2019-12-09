package explore.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(value, statistics.getSum());
        assertEquals(0D, statistics.getVariance(), 1e-10);
        assertEquals(1, statistics.getSampleSize());
    }

    @Test
    void update_singleUpdate_allStatisticsAreCorrect() {
        statistics.update(5);
        assertEquals(3, statistics.getMean(), 1e-10);
        assertEquals(6, statistics.getSum(), 1e-10);
        assertEquals(4, statistics.getVariance(), 1e-10);
        assertEquals(2, statistics.getStandardDeviation(), 1e-10);
        assertEquals(2, statistics.getSampleSize());
    }

    @Test
    void update_twoUpdates_allStatisticsAreCorrect() {
        statistics.update(5);
        statistics.update(-3);
        assertEquals(1, statistics.getMean(), 1e-10);
        assertEquals(3, statistics.getSum(), 1e-10);
        assertEquals(32.0 / 3, statistics.getVariance(), 1e-10);
        assertEquals(Math.sqrt(32.0 / 3), statistics.getStandardDeviation(), 1e-10);
        assertEquals(3, statistics.getSampleSize());
    }

    @Test
    void update_threeUpdates_allStatisticsAreCorrect() {
        double[] values = new double[] {5, -3, 10, -22, 101};
        for (double val: values) {
            statistics.update(val);
        }
        assertEquals(15.333333333333, statistics.getMean(), 1e-10);
        //assertEquals(3, statistics.getSum(), 1e-10);
        assertEquals(1568.2222222222, statistics.getVariance(), 1e-10);
        assertEquals(39.6007856263, statistics.getStandardDeviation(), 1e-10);
        assertEquals(6, statistics.getSampleSize());
    }
}