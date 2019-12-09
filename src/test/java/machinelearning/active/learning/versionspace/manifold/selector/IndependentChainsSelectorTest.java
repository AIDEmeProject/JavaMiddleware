package machinelearning.active.learning.versionspace.manifold.selector;

import machinelearning.active.learning.versionspace.manifold.HitAndRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class IndependentChainsSelectorTest extends AbstractSampleSelectorTest {
    private final int numSamples = 3;
    private final int chainLength = 10;
    private HitAndRun hitAndRun;
    private HitAndRun.Chain chain;

    @BeforeEach
    void setUp() {
        sampleSelector = new IndependentChainsSelector(chainLength);

        chain = mock(HitAndRun.Chain.class);
        when(chain.advance(chainLength)).thenReturn(Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2));

        hitAndRun = mock(HitAndRun.class);
        when(hitAndRun.newChain()).thenReturn(chain);
    }

    @Test
    void constructor_negativeChainLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new IndependentChainsSelector(-1));
    }

    @Test
    void constructor_zeroChainLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new IndependentChainsSelector(0));
    }

    @Test
    void select_sampleThreePoints_newChainCalledOncePerSample() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(hitAndRun, times(numSamples)).newChain();
    }

    @Test
    void select_sampleThreePoints_generateThreeChainsOfSizeEqualToChainLength() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(chain, times(numSamples)).advance(chainLength);
    }

    @Test
    void select_stubbedHitAndRunChainResults_selectReturnsStubbedSamples() {
        Vector[] result = sampleSelector.select(hitAndRun, numSamples);
        assertArrayEquals(new Vector[] {Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2)}, result);
    }
}