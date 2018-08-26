package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HitAndRunSamplerTest {
    private DirectionSamplingAlgorithm directionSamplingAlgorithm;
    private HitAndRunSampler sampler;

    @BeforeEach
    void setUp() {
        directionSamplingAlgorithm =  mock(DirectionSamplingAlgorithm.class);
        sampler = new HitAndRunSampler(directionSamplingAlgorithm, new Random());
    }

    @Test
    void newChain_mockDirectionSamplingAlgorithm_fitCalledOnce() {
        ConvexBody body = mock(ConvexBody.class);

        sampler.newChain(body);

        verify(directionSamplingAlgorithm).fit(body);
    }
}