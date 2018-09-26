package machinelearning.active.learning.versionspace.convexbody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    private Vector center;
    private Vector direction;
    private Line line;

    @BeforeEach
    void setUp() {
        center = Vector.FACTORY.make(3, -2);
        direction = Vector.FACTORY.make(-1, 1);
        line = new Line(center, direction);
    }

    @Test
    void constructor_CenterAndDirectionHaveIncompatibleLengths_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Line(center, Vector.FACTORY.make(1)));
    }

    @Test
    void constructor_DirectionIsZeroVector_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Line(center, Vector.FACTORY.zeros(2)));
    }

    @Test
    void getDim_TwoDimensionalLine_CorrectDimensionReturned() {
        assertEquals(center.dim(), line.getDim());
    }

    @Test
    void getPoint_PositionEqualsToZero_CenterIsReturned() {
        assertEquals(center, line.getPoint(0.));
    }

    @Test
    void getPoint_PositionDifferentFromZero_CorrectPointIsReturned() {
        assertEquals(Vector.FACTORY.make(2, -1), line.getPoint(1.));
    }

    @Test
    void getSegment_infiniteLeftBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> line.getSegment(Double.NEGATIVE_INFINITY, 1));
    }

    @Test
    void getSegment_infiniteRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> line.getSegment(Double.POSITIVE_INFINITY, 1));
    }

    @Test
    void getSegment_leftBoundEqualsRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> line.getSegment(1, 1));
    }

    @Test
    void getSegment_leftBoundLargerThanRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> line.getSegment(2, 1));
    }

    @Test
    void getSegment_validInput_returnsExpectedLineSegment() {
        LineSegment segment = line.getSegment(-2, 3);
        assertEquals(-2, segment.getLeftBound());
        assertEquals(3, segment.getRightBound());
    }
}