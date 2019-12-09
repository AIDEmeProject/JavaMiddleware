package explore.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatisticsCollectionTest {
    private StatisticsCollection collection;

    @BeforeEach
    void setUp() {
        collection = new StatisticsCollection();
        collection.update("metric1", 1.0);
    }

    @Test
    void get_nameNotInCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> collection.get("unknown"));
    }

    @Test
    void get_nameInCollection_correctStatisticRetrieved() {
        assertEquals("metric1", collection.get("metric1").getName());
    }

    @Test
    void update_nameNotInCollection_newStatisticAppended() {
        collection.update("metric2", 2.0);
        Statistics statistics = collection.get("metric2");
        assertEquals(2.0, statistics.getMean(), 1e-10);
        assertEquals(1, statistics.getSampleSize());
        assertEquals(0.0, statistics.getVariance(), 1e-10);
    }

    @Test
    void update_nameInCollection_statisticCorrectlyUpdated() {
        collection.update("metric1", 5.0);
        Statistics statistics = collection.get("metric1");
        assertEquals(3, statistics.getMean(), 1e-10);
        assertEquals(4, statistics.getVariance(), 1e-10);
        assertEquals(2, statistics.getSampleSize());
    }
}