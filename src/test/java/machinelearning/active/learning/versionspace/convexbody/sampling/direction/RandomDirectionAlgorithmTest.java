package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.RandomDirectionSampler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomDirectionAlgorithmTest {

    @Test
    void fit_twoDimensionalConvexBodyStub_returnsTwoDimensionalRandomDirectionSampler() {
        ConvexBody body = mock(ConvexBody.class);
        when(body.getDim()).thenReturn(2);

        RandomDirectionAlgorithm factory = new RandomDirectionAlgorithm();

        assertEquals(new RandomDirectionSampler(2), factory.fit(body));
    }
}