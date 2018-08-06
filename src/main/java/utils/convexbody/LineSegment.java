package utils.convexbody;

import utils.Validator;

import java.util.Random;

/**
 * A line segment is a compact, connected subset of a straight line. In mathematical terms, let a straight line be defined
 * by a point X0 and its direction V. A line segment is on the form:
 *
 *                  X(t) = X0 + t * V, for L <= t <= R
 *
 * where L and R are the left and right bounds on the segment respectively.
 *
 * @see Line
 */
public class LineSegment {
    /**
     * Line segment
     */
    private Line line;

    /**
     * Left bound L of line segment
     */
    private double leftBound;

    /**
     * Right bound of line segment
     */
    private double rightBound;

    /**
     * @param line: straight line the segment is a subset of
     * @param leftBound: left bound L in the line segment definition
     * @param rightBound: right bound R in the line segment definition
     * @throws IllegalArgumentException if leftBound is not smaller than rightBound
     */
     LineSegment(Line line, double leftBound, double rightBound) {
        Validator.assertIsFinite(leftBound);
        Validator.assertIsFinite(rightBound);

        if (leftBound >= rightBound){
            throw new IllegalArgumentException("Left bound must be smaller than right bound.");
        }

        this.line = line;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    /**
     * @return a random point in the line segment.
     */
    public double[] sampleRandomPoint(){
        // TODO: how to set this seed
        Random rand = new Random();
        return line.getPoint(leftBound + rand.nextDouble() * (rightBound - leftBound));
    }
}
