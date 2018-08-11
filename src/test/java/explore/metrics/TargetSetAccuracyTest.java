package explore.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TargetSetAccuracyTest {
    private TargetSetAccuracy metric;

    @Test
    void constructor_NegativeNumberOfTargetsRetrieved_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TargetSetAccuracy(-1, 1));
    }

    @Test
    void constructor_NegativeTotalNumberOfTargetsRetrieved_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TargetSetAccuracy(1, -1));
    }

    @Test
    void constructor_ZeroTotalNumberOfTargetsRetrieved_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TargetSetAccuracy(1, -1));
    }

    @Test
    void constructor_MoreTargetsRetrievedThanNumberOfTargets_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TargetSetAccuracy(2, 1));
    }

    @Test
    void getTargetSetAccuracy_ZeroTargetsRetrieved_returnsZero() {
        metric = new TargetSetAccuracy(0, 10);
        assertEquals(0.0 , metric.targetSetAccuracy());
    }

    @Test
    void getTargetSetAccuracy_AllTargetsRetrieved_returnsOne() {
        metric = new TargetSetAccuracy(10, 10);
        assertEquals(1.0 , metric.targetSetAccuracy());
    }

    @Test
    void getTargetSetAccuracy_SomeTargetsRetrieved_ReturnsCorrectValue() {
        metric = new TargetSetAccuracy(4, 10);
        assertEquals(0.4 , metric.targetSetAccuracy());
    }
}