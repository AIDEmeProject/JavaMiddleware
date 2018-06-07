package explore;

import exceptions.IncompatibleMetricsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMetricsTest {

    private ExplorationMetrics explorationMetrics;

    @BeforeEach
    void setUp() {
        explorationMetrics = new ExplorationMetrics();

        Metrics metrics = new Metrics();
        metrics.add("accuracy", 1.0);
        explorationMetrics.add(metrics);
    }

    @Test
    void isEmpty_noMetricsAdded_returnsTrue() {
        assertTrue(new ExplorationMetrics().isEmpty());
    }

    @Test
    void isEmpty_oneMetricsAdded_returnsFalse() {
        assertFalse(explorationMetrics.isEmpty());
    }

    @Test
    void size_noMetricsAdded_returnsZero() {
        assertEquals(0, new ExplorationMetrics().size());
    }

    @Test
    void size_oneMetricsAdded_returnsOne() {
        assertEquals(1, explorationMetrics.size());
    }

    @Test
    void sum_twoEmptyExplorationMetrics_returnEmptyMetric() {
        ExplorationMetrics explorationMetrics1 = new ExplorationMetrics();
        ExplorationMetrics explorationMetrics2 = new ExplorationMetrics();
        assertTrue(explorationMetrics1.sum(explorationMetrics2).isEmpty());
    }

    @Test
    void sum_differentSizesExplorationMetrics_throwsException() {
        ExplorationMetrics explorationMetrics1 = new ExplorationMetrics();
        assertThrows(IllegalArgumentException.class, () -> explorationMetrics.sum(explorationMetrics1));
    }

    @Test
    void sum_twoNonEmptyIncompatibleExplorationMetrics_throwsException() {
        Metrics metrics = new Metrics();
        metrics.add("accuracy1", 0.5);

        ExplorationMetrics explorationMetrics1 = new ExplorationMetrics();
        explorationMetrics1.add(metrics);

        assertThrows(IncompatibleMetricsException.class, () -> explorationMetrics.sum(explorationMetrics1));
    }

    @Test
    void sum_twoNonEmptyCompatibleExplorationMetrics_correctlyComputesSum() {
        Metrics metrics = new Metrics();
        metrics.add("accuracy", 0.5);

        ExplorationMetrics explorationMetrics1 = new ExplorationMetrics();
        explorationMetrics1.add(metrics);

        ExplorationMetrics result = new ExplorationMetrics();
        metrics = new Metrics();
        metrics.add("accuracy", 1.5);
        result.add(metrics);

        assertEquals(result, explorationMetrics.sum(explorationMetrics1));
    }

    @Test
    void divideByNumber_ZeroDenominator_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> explorationMetrics.divideByNumber(0));
    }

    @Test
    void divideByNumber_emptyExplorationMetric_returnsEmptyMetric() {
        assertTrue(ExplorationMetrics.divideByNumber(new ExplorationMetrics(), 10).isEmpty());
    }

    @Test
    void divideByNumber_nonEmptyExplorationMetric_returnsAllMetricValuesDivided() {
        Metrics metrics = new Metrics();
        metrics.add("accuracy", 0.1);

        ExplorationMetrics result = new ExplorationMetrics();
        result.add(metrics);

        assertEquals(result, explorationMetrics.divideByNumber(10));
    }
}