package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomSamplingAlgorithmTest {

    @Test
    void fit_twoDimensionalConvexBodyStub_returnsTwoDimensionalRandomDirectionSampler() {
        ConvexBody body = mock(ConvexBody.class);
        when(body.getDim()).thenReturn(2);

        RandomSamplingAlgorithm factory = new RandomSamplingAlgorithm();

        assertEquals(new RandomDirectionSampler(2), factory.fit(body));
    }
}