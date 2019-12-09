package machinelearning.active.learning.versionspace.manifold;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class GeodesicTest {
    protected Vector center;
    protected Vector direction;
    protected Geodesic geodesic;

    @BeforeEach
    void setUp() {
        center = Vector.FACTORY.make(3, -2);
        direction = Vector.FACTORY.make(-1, 1);
        geodesic = getInstance(center, direction);
    }

    public abstract Geodesic getInstance(Vector center, Vector direction);

    @Test
    void constructor_CenterAndDirectionHaveIncompatibleDimensions_ThrowsException() {
        assertThrows(RuntimeException.class, () -> getInstance(Vector.FACTORY.make(2, 2), Vector.FACTORY.make(1)));
    }

    @Test
    void constructor_DirectionIsZeroVector_ThrowsException() {
        assertThrows(RuntimeException.class, () -> getInstance(Vector.FACTORY.make(1, 2), Vector.FACTORY.zeros(2)));
    }

    @Test
    void getDim_TwoDimensionalCenterVector_ReturnsTwo() {
        assertEquals(center.dim(), geodesic.dim());
    }

    @Test
    void getPoint_PositionEqualsToZero_CenterIsReturned() {
        assertEquals(center, geodesic.getPoint(0.));
    }

    @Test
    public abstract void getPoint_PositionDifferentFromZero_CorrectPointIsReturned();

    @Test
    void getSegment_infiniteLeftBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(Double.NEGATIVE_INFINITY, 1));
    }

    @Test
    void getSegment_infiniteRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(Double.POSITIVE_INFINITY, 1));
    }

    @Test
    void getSegment_leftBoundEqualsRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(1, 1));
    }

    @Test
    void getSegment_leftBoundLargerThanRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(2, 1));
    }

    @Test
    void getSegment_validInput_returnsExpectedGeodesicSegment() {
        GeodesicSegment segment = geodesic.getSegment(-2, 3);
        assertEquals(new GeodesicSegment(geodesic, -2, 3), segment);
    }
}