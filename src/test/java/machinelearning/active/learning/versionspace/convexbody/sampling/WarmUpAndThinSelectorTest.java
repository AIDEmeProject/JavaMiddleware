package machinelearning.active.learning.versionspace.convexbody.sampling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WarmUpAndThinSelectorTest extends AbstractSampleSelectorTest {
    private final int warmUp = 10;
    private final int thin = 6;
    private final int numSamples = 3;
    private HitAndRunChain chain;

    @BeforeEach
    void setUp() {
        sampleSelector = new WarmUpAndThinSelector(warmUp, thin);

        chain = mock(HitAndRunChain.class);
        when(chain.advance(anyInt())).thenReturn(new double[]{0}, new double[]{1}, new double[]{2});
        when(chain.copy()).thenReturn(chain);
    }

    @Test
    void constructor_negativeWarmup_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new WarmUpAndThinSelector(-1, 1));
    }

    @Test
    void constructor_zeroThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new WarmUpAndThinSelector(1, 0));
    }

    @Test
    void constructor_negativeThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new WarmUpAndThinSelector(1, -1));
    }

    @Test
    void select_mockedHitAndRunChain_copyNeverCalled() {
        sampleSelector.select(chain, numSamples);
        verify(chain, never()).copy();
    }

    @Test
    void select_mockedHitAndRunChain_advanceCalledOnceForWarmUpAndRemainingTimesForThinning() {
        sampleSelector.select(chain, numSamples);
        verify(chain).advance(warmUp);
        verify(chain, times(numSamples-1)).advance(thin);
    }

    @Test
    void select_stubbedHitAndRunChainResults_selectReturnsStubbedSamples() {
        double[][] result = sampleSelector.select(chain, numSamples);
        assertArrayEquals(new double[][] {{0}, {1}, {2}}, result);
    }
}