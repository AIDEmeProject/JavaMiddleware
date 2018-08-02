package explore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetricsTest {
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
        metrics.put("metric1", 1.0);
        metrics.put("metric2", 2.0);
    }

    @Test
    void get_nameNotInMetrics_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> metrics.get("unknown"));
    }

    @Test
    void get_nameInMetrics_expectedValueReturned() {
        assertEquals(metrics.get("metric1"), (Double) 1.0);
        assertEquals(metrics.get("metric2"), (Double) 2.0);
    }

    @Test
    void put_nameNotInMetrics_newMetricCorrectlyAdded() {
        metrics.put("metric3", 3.0);
        assertEquals(metrics.get("metric3"), (Double) 3.0);
    }

    @Test
    void put_nameAlreadyInMetrics_previousValueIsOverwritten() {
        metrics.put("metric1", 10.0);
        assertEquals(metrics.get("metric1"), (Double) 10.0);
    }

    @Test
    void fromJson_noLabeledPoints_metricsCorrectlyParsed() {
        String json = "{\"points\": [], \"metric1\": 1.0, \"metric2\": 2.0}";
        Metrics metrics = Metrics.fromJson(json);
        assertEquals(metrics.get("metric1"), (Double) 1.0);
        assertEquals(metrics.get("metric2"), (Double) 2.0);
    }

    @Test
    void toString_noLabeledPoints_expectedJSONString() {
        assertEquals("{\"points\": [], \"metric1\": 1.0, \"metric2\": 2.0}", metrics.toString());
    }
}