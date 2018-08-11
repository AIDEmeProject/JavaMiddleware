package explore.metrics;

import data.LabeledPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TargetSetAccuracyCalculatorTest {
    private TargetSetAccuracyCalculator calculator;
    private TargetSetAccuracy metric;
    private Collection<LabeledPoint> points;
    private int[] labels;

    @BeforeEach
    void setUp() {
        calculator = new TargetSetAccuracyCalculator();
        points = new ArrayList<>();
        labels = new int[] {0,0,1,0,1};
    }

    @Test
    void compute_allTargetsRetrieved_targetSetAccuracyCorrectlyComputed() {
        for (int i = 0; i < labels.length; i++) {
            points.add(new LabeledPoint(i, new double[] {i}, labels[i]));
        }
        metric = calculator.compute(points, labels);
        assertEquals(2, metric.getTotalNumberOfTargets());
        assertEquals(2, metric.getNumberOfTargetsRetrieved());
    }

    @Test
    void compute_noRowsRetrieved_targetSetAccuracyCorrectlyComputed() {
        metric = calculator.compute(points, labels);
        assertEquals(2, metric.getTotalNumberOfTargets());
        assertEquals(0, metric.getNumberOfTargetsRetrieved());
    }

    @Test
    void compute_oneTargetRetrieved_targetSetAccuracyCorrectlyComputed() {
        points.add(new LabeledPoint(2, new double[] {2}, labels[2]));
        metric = calculator.compute(points, labels);
        assertEquals(2, metric.getTotalNumberOfTargets());
        assertEquals(1, metric.getNumberOfTargetsRetrieved());
    }
}