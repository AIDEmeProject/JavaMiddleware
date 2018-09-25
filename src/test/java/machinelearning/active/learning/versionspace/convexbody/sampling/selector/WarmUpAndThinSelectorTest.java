package machinelearning.active.learning.versionspace.convexbody.sampling.selector;

import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WarmUpAndThinSelectorTest extends AbstractSampleSelectorTest {
    private final int warmUp = 10;
    private final int thin = 6;
    private final int numSamples = 3;
    private HitAndRun hitAndRun;
    private HitAndRun.Chain chain;

    @BeforeEach
    void setUp() {
        sampleSelector = new WarmUpAndThinSelector(warmUp, thin);

        chain = mock(HitAndRun.Chain.class);
        when(chain.advance(anyInt())).thenReturn(Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2));

        hitAndRun = mock(HitAndRun.class);
        when(hitAndRun.newChain()).thenReturn(chain);
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
    void select_mockedHitAndRunChain_newChainCalledOnce() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(hitAndRun).newChain();
    }

    @Test
    void select_mockedHitAndRunChain_advanceCalledOnceForWarmUpAndRemainingTimesForThinning() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(chain).advance(warmUp);
        verify(chain, times(numSamples-1)).advance(thin);
    }

    @Test
    void select_stubbedHitAndRunChainResults_selectReturnsStubbedSamples() {
        Vector[] result = sampleSelector.select(hitAndRun, numSamples);
        assertArrayEquals(new Vector[] {Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2)}, result);
    }
}