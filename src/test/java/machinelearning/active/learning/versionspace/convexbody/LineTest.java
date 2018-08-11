package machinelearning.active.learning.versionspace.convexbody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {
    private double[] center;
    private double[] direction;
    private Line line;

    @BeforeEach
    void setUp() {
        center = new double[] {3,-2};
        direction = new double[] {-1,1};
        line = new Line(center, direction);
    }

    @Test
    void constructor_CenterAndDirectionHaveIncompatibleLengths_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Line(center, new double[] {1}));
    }

    @Test
    void constructor_CenterIsEmptyArray_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Line(new double[0], direction));
    }

    @Test
    void constructor_DirectionIsEmptyArray_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Line(center, new double[0]));
    }

    @Test
    void constructor_DirectionIsZeroVector_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Line(center, new double[] {0,0}));
    }

    @Test
    void getDim_TwoDimensionalLine_CorrectDimensionReturned() {
        assertEquals(center.length, line.getDim());
    }

    @Test
    void getPoint_PositionEqualsToZero_CenterIsReturned() {
        assertArrayEquals(center, line.getPoint(0.));
    }

    @Test
    void getPoint_PositionDifferentFromZero_CorrectPointIsReturned() {
        assertArrayEquals(new double[] {2, -1}, line.getPoint(1.));
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
    void sampleRandomLine_EmptyInputArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Line.sampleRandomLine(new double[0]));
    }
}