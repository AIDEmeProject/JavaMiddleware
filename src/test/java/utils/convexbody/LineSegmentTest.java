package utils.convexbody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineSegmentTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line(new double[2], new double[] {1,2});
    }

    @Test
    void constructor_leftBoundEqualsRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(line, 1, 1));
    }

    @Test
    void constructor_leftBoundLargerThanRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(line,2, 1));
    }
}