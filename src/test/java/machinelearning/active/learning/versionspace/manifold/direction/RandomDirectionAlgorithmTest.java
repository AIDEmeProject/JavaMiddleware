package machinelearning.active.learning.versionspace.manifold.direction;


import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomDirectionAlgorithmTest {

    @Test
    void fit_twoDimensionalConvexBodyStub_returnsTwoDimensionalRandomDirectionSampler() {
        Manifold manifold = mock(Manifold.class);
        ConvexBody body = mock(ConvexBody.class);
        when(body.getManifold()).thenReturn(manifold);

        RandomDirectionAlgorithm factory = new RandomDirectionAlgorithm();

        assertEquals(new RandomDirectionSampler(manifold), factory.fit(body));
    }
}