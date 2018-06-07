package metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class TargetSetAccuracyCalculatorTest {
    private TargetSetAccuracyCalculator calculator;
    private TargetSetAccuracy metric;
    private Collection<Integer> rows;
    private int[] labels;

    @BeforeEach
    void setUp() {
        calculator = new TargetSetAccuracyCalculator();
        rows = new ArrayList<>();
        labels = new int[] {0,0,1,0,1};
    }

    @Test
    void compute_allTargetsRetrieved_targetSetAccuracyCorrectlyComputed() {
        for (int i = 0; i < labels.length; i++) {
            rows.add(i);
        }
        metric = calculator.compute(rows, labels);
        assertEquals(2, metric.getTotalNumberOfTargets());
        assertEquals(2, metric.getNumberOfTargetsRetrieved());
    }

    @Test
    void compute_noRowsRetrieved_targetSetAccuracyCorrectlyComputed() {
        metric = calculator.compute(rows, labels);
        assertEquals(2, metric.getTotalNumberOfTargets());
        assertEquals(0, metric.getNumberOfTargetsRetrieved());
    }

    @Test
    void compute_oneTargetRetrieved_targetSetAccuracyCorrectlyComputed() {
        rows.add(2);
        metric = calculator.compute(rows, labels);
        assertEquals(2, metric.getTotalNumberOfTargets());
        assertEquals(1, metric.getNumberOfTargetsRetrieved());
    }
}