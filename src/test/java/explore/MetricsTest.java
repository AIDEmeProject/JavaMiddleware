package explore;

import exceptions.IncompatibleMetricsException;
import exceptions.MetricAlreadyExistsException;
import exceptions.MetricNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MetricsTest {
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
        metrics.add("accuracy", 1.0);
    }

    @Test
    void isEmpty_emptyMetric_returnsTrue() {
        assertTrue(new Metrics().isEmpty());
    }

    @Test
    void isEmpty_nonEmptyMetric_returnsFalse() {
        assertFalse(metrics.isEmpty());
    }

    @Test
    void get_metricNameNotInObject_throwsException() {
        assertThrows(MetricNotFoundException.class, () -> metrics.get("accuracy1"));
    }

    @Test
    void get_metricNameAlreadyInObject_returnsExpectedValue() {
        assertEquals((Double) 1.0, metrics.get("accuracy"));
    }

    @Test
    void add_addMetricAlreadyInObject_throwsException() {
        assertThrows(MetricAlreadyExistsException.class, () -> metrics.add("accuracy", 0.5));
    }

    @Test
    void size_emptyMetricAdded_returnsZero() {
        assertEquals(0, new Metrics().size());
    }

    @Test
    void size_oneElementsAdded_returnsOne() {
        assertEquals(1, metrics.size());
    }

    @Test
    void addAll_addEmptyMetric_noMetricAdded() {
        int size = metrics.size();
        metrics.addAll(new Metrics());
        assertEquals(size, metrics.size());
    }

    @Test
    void addAll_addMetricWithTheSameMetricNames_throwsException() {
        Metrics metrics1 = new Metrics();
        metrics1.add("accuracy", 0.5);
        assertThrows(MetricAlreadyExistsException.class, () -> metrics.addAll(metrics1));
    }

    @Test
    void addAll_addMetricWithDifferentMetricNames_throwsException() {
        Metrics metrics1 = new Metrics();
        metrics1.add("accuracy1", 0.5);
        metrics1.addAll(metrics);

        Metrics result = new Metrics();
        result.add("accuracy", 1.0);
        result.add("accuracy1", 0.5);

        assertEquals(result, metrics1);
    }

    @Test
    void names_noMetricsAdded_returnsEmptySet() {
        assertTrue(new Metrics().names().isEmpty());
    }

    @Test
    void names_oneMetricsAdded_returnsSetContainingSingleElement() {
        Set<String> names = new HashSet<>();
        names.add("accuracy");
        assertEquals(names, metrics.names());
    }

    @Test
    void sum_twoEmptyMetrics_returnEmptyMetric() {
        Metrics metrics1 = new Metrics(), metrics2 = new Metrics();
        assertTrue(metrics1.sum(metrics2).isEmpty());
    }

    @Test
    void sum_oneEmptyMetric_throwsException() {
        assertThrows(IncompatibleMetricsException.class, () -> metrics.sum(new Metrics()));
    }

    @Test
    void sum_twoNonEmptyMetricsOfDifferentMetricsNames_throwsException() {
        Metrics metrics1 = new Metrics();
        metrics1.add("accuracy1", 0.5);
        assertThrows(IncompatibleMetricsException.class, () -> metrics.sum(metrics1));
    }

    @Test
    void sum_twoNonEmptyMetricsOfSameMetricNames_computesSumCorrectly() {
        Metrics metrics1 = new Metrics(), result = new Metrics();
        metrics1.add("accuracy", 0.5);
        result.add("accuracy", 1.5);
        assertEquals(result, metrics.sum(metrics1));
    }

    @Test
    void divideByNumber_ZeroDenominator_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> metrics.divideByNumber(0));
    }

    @Test
    void divideByNumber_emptyMetric_returnsEmptyMetric() {
        assertTrue(Metrics.divideByNumber(new Metrics(), 10).isEmpty());
    }

    @Test
    void divideByNumber_nonEmptyMetric_returnsMetricValuesDivided() {
        Metrics result = new Metrics();
        result.add("accuracy", 0.1);
        assertEquals(result, metrics.divideByNumber(10));
    }
}