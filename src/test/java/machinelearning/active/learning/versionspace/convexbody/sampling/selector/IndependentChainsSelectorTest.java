package machinelearning.active.learning.versionspace.convexbody.sampling.selector;

import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class IndependentChainsSelectorTest extends AbstractSampleSelectorTest {
    private final int numSamples = 3;
    private final int chainLength = 10;
    private HitAndRunChain chain;

    @BeforeEach
    void setUp() {
        sampleSelector = new IndependentChainsSelector(chainLength);

        chain = mock(HitAndRunChain.class);
        when(chain.advance(chainLength)).thenReturn(new double[]{0}, new double[]{1}, new double[]{2});
        when(chain.copy()).thenReturn(chain);
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
    void select_sampleThreePoints_copyCalledOncePerSample() {
        sampleSelector.select(chain, numSamples);
        verify(chain, times(numSamples)).copy();
    }

    @Test
    void select_sampleThreePoints_generateThreeChainsOfSizeGivenByChainLengthParameter() {
        sampleSelector.select(chain, numSamples);
        verify(chain, times(numSamples)).advance(chainLength);
    }

    @Test
    void select_stubbedHitAndRunChainResults_selectReturnsStubbedSamples() {
        double[][] result = sampleSelector.select(chain, numSamples);
        assertArrayEquals(new double[][] {{0}, {1}, {2}}, result);
    }
}