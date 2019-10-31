package machinelearning.active.learning.versionspace.manifold.selector;

import machinelearning.active.learning.versionspace.manifold.HitAndRun;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

abstract class AbstractSampleSelectorTest {
    protected SampleSelector sampleSelector;

    @Test
    void select_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampleSelector.select(mock(HitAndRun.class), -1));
    }

    @Test
    void select_zeroNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampleSelector.select(mock(HitAndRun.class), 0));
    }
}