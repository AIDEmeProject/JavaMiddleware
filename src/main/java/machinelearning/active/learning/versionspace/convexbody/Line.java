package machinelearning.active.learning.versionspace.convexbody;

import utils.Validator;
import utils.linalg.Vector;

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
    private Vector center;

    /**
     * The line's direction
     */
    private Vector direction;

    /**
     * @param center: any point on the line
     * @param direction: the direction of the line. It will NOT be normalized or changed anyhow.
     * @throws IllegalArgumentException if center and direction have different lengths, or direction is the zero vector
     */
    public Line(Vector center, Vector direction) {
        Validator.assertEquals(center.dim(), direction.dim());
        if (direction.squaredNorm() == 0) {
            throw new IllegalArgumentException("Direction cannot be zero vector.");
        }

        this.center = center;
        this.direction = direction;
    }

    public Vector getCenter() {
        return center;
    }

    public Vector getDirection() {
        return direction;
    }

    /**
     * @return the dimension of the euclidean space containing this line
     */
    public int getDim(){
        return center.dim();
    }

    /**
     * @param t: position of the requested point on the line
     * @return the point center + t * direction
     */
    public Vector getPoint(double t){
        return center.add(direction.scalarMultiply(t));
    }

    /**
     * Returns the segment: X(t) = center + t * direction, for \(leftBound \leq t \leq rightBound\)
     * @param leftBound: left bound of line segment
     * @param rightBound: right bound of line segment
     * @return a line segment on this line
     * @throws IllegalArgumentException if rightBound is smaller than leftBound, or any of the bounds are not finite
     * @see LineSegment
     */
    public LineSegment getSegment(double leftBound, double rightBound){
        return new LineSegment(this, leftBound, rightBound);
    }
}
