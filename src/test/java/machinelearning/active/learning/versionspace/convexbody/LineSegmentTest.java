package machinelearning.active.learning.versionspace.convexbody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineSegmentTest {
    private Line line;
    private LineSegment segment;

    @BeforeEach
    void setUp() {
        line = new Line(new double[2], new double[] {1,2});
        segment = new LineSegment(line, -1, 2);
    }

    @Test
    void constructor_leftBoundEqualsNegativeInfinity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(line, Double.NEGATIVE_INFINITY, 1));
    }

    @Test
    void constructor_rightBoundEqualsPositiveInfinity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(line, 0, Double.POSITIVE_INFINITY));
    }

    @Test
    void constructor_leftBoundEqualsRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(line, 1, 1));
    }

    @Test
    void constructor_leftBoundLargerThanRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(line,2, 1));
    }

    @Test
    void getPoint_negativeProportion_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> segment.getPoint(-1));
    }

    @Test
    void getPoint_largerThanOneProportion_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> segment.getPoint(2));
    }

    @Test
    void getPoint_zeroProportion_returnsLeftExtreme() {
        assertArrayEquals(new double[] {-1, -2}, segment.getPoint(0));
    }

    @Test
    void getPoint_OneProportion_returnsRightExtreme() {
        assertArrayEquals(new double[] {2, 4}, segment.getPoint(1));
    }

    @Test
    void getPoint_halfProportion_returnsMiddlePoint() {
        assertArrayEquals(new double[] {0.5, 1}, segment.getPoint(0.5));
    }
}