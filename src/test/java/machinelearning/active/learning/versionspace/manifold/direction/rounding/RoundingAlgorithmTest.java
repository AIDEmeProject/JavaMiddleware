package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class RoundingAlgorithmTest {

    private RoundingAlgorithm rounding;

    @BeforeEach
    void setUp() {
        rounding = new RoundingAlgorithm(Long.MAX_VALUE);
    }

    @Test
    void constructor_negativeNumberOfIterations_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new RoundingAlgorithm(-1));
    }

    @Test
    void constructor_zeroNumberOfIterations_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new RoundingAlgorithm(0));
    }

    @Test
    void fit_ellipsoidCutsNeverStop_reduceEllipsoidCalledMaxIterTimes() {
        int maxIter = 10;
        rounding = new RoundingAlgorithm(maxIter);

        Ellipsoid elp = mock(Ellipsoid.class);

        ConvexBody body = mock(ConvexBody.class);
        when(body.getContainingEllipsoid()).thenReturn(elp);
        when(body.attemptToReduceEllipsoid(elp)).thenReturn(true);

        rounding.fit(body);

        verify(body, times(maxIter)).attemptToReduceEllipsoid(elp);
    }

    @Test
    void fit_ellipsoidCutsReturnsFalseAfterThreeIterations_reduceEllipsoidCalledThreeTimes() {
        Ellipsoid elp = mock(Ellipsoid.class);

        ConvexBody body = mock(ConvexBody.class);
        when(body.getContainingEllipsoid()).thenReturn(elp);
        when(body.attemptToReduceEllipsoid(elp)).thenReturn(true, true, false);

        rounding.fit(body);

        verify(body, times(3)).attemptToReduceEllipsoid(any());
    }
}