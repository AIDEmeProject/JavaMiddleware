package utils.convexbody;

/**
 * This interface represents an OPEN, BOUNDED, CONVEX subset in the euclidean space. More specifically, a subset K of
 * the euclidean space is called convex if:
 *
 *      for all x1, x2 in K, the point t * x1 + (1-t) * x2 is also in K, for 0 <= t <= 1
 *
 * However, since this condition cannot be algorithmically verified, it is up to each implementation to guarantee it
 * correctly defines such a mathematical object.
 *
 *
 */
public interface ConvexBody {
    /**
     * @param x: a point in the euclidean space
     * @return whether the point x is inside or outside the convex body
     */
    boolean isInside(double[] x);

    /**
     * @return any point inside the convex body
     */
    double[] getInteriorPoint();

    /**
     * @param line: straight line intersecting the convex body
     * @return the line segment representing the intersection between this convex body and a straight line
     * @throws RuntimeException if the line does not intersect this body, or the intersection is a single point
     */
    LineSegment computeLineIntersection(Line line);
}
