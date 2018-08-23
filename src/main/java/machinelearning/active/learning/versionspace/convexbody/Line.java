package machinelearning.active.learning.versionspace.convexbody;

import utils.Validator;
import utils.linalg.LinearAlgebra;

/**
 * This class represents a Line Segment in euclidean space. A line is defined by two elements:
 *
 *  - Any point C in the line segment, which we call center
 *  - The direction vector D
 *
 * Mathematically, any point X on the line can be written as: X(t) = C + t * D, where t is a real number.
 *
 * @see LineSegment
 */
public class Line {
    /**
     * A point on the line
     */
    private double[] center;

    /**
     * The line's direction
     */
    private double[] direction;

    /**
     * @param center: any point on the line
     * @param direction: the direction of the line. It will NOT be normalized or changed anyhow.
     * @throws IllegalArgumentException if center and direction have different lengths, if they are empty arrays, or direction is the zero vector
     */
    public Line(double[] center, double[] direction) {
        Validator.assertNotEmpty(center);
        Validator.assertEqualLengths(center, direction);
        Validator.assertPositive(LinearAlgebra.sqNorm(direction));

        this.center = center;
        this.direction = direction;
    }

    public double[] getCenter() {
        return center;
    }

    public double[] getDirection() {
        return direction;
    }

    /**
     * @return the dimension of the euclidean space containing this line
     */
    public int getDim(){
        return center.length;
    }

    /**
     * @param t: position of the requested point on the line
     * @return the point center + t * direction
     */
    public double[] getPoint(double t){
        double[] point = new double[center.length];

        for (int i = 0; i < center.length; i++) {
            point[i] = center[i] + t * direction[i];
        }

        return point;
    }

    /**
     * Returns the segment: X(t) = center + t * direction, for \(leftBound \leq t \leq rightBound\)
     * @param leftBound: left bound of line segment
     * @param rightBound: right bound of line segment
     * @return a line segment on this line
     * @see LineSegment
     */
    public LineSegment getSegment(double leftBound, double rightBound){
        return new LineSegment(this, leftBound, rightBound);
    }
}
