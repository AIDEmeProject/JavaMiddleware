package machinelearning.active.learning.versionspace.convexbody.sampling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

abstract class AbstractSampleSelectorTest {
    protected SampleSelector sampleSelector;

    @Test
    void select_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampleSelector.select(mock(HitAndRunChain.class), -1));
    }

    @Test
    void select_zeroNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampleSelector.select(mock(HitAndRunChain.class), 0));
    }
}