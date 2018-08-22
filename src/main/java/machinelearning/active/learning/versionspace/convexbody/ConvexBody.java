package machinelearning.active.learning.versionspace.convexbody;

import machinelearning.classifier.margin.LinearClassifier;

import java.util.Optional;

/**
 * This interface represents a CLOSED, BOUNDED, CONVEX subset in the euclidean space. More specifically, a subset K of
 * the euclidean space is called convex if:
 *
 *      \(\forall x1, x2 \in K \text{ and }  0 \leq t \leq 1 \, t * x1 + (1-t) * x2 \in K \)
 *
 * However, since this condition cannot be algorithmically verified, it is up to each implementation to guarantee it
 * correctly defines such a mathematical object.
 *
 *
 */
public interface ConvexBody {
    /**
     * @return dimension of the enclosing euclidean space
     */
    int getDim();

    /**
     * @param x: a point in the euclidean space
     * @return whether the point x is inside or outside the convex body
     * @throws IllegalArgumentException if x.length and getDim() are different
     */
    boolean isInside(double[] x);

    /**
     * @return any point in the interior of the convex body
     */
    double[] getInteriorPoint();

    /**
     * @param line: straight line intersecting the convex body
     * @return the line segment representing the intersection between this convex body and a straight line
     * @throws RuntimeException if the line does not intersect this body, or the intersection is a single point
     * @throws IllegalArgumentException if line dimension is different from getDim()
     */
    LineSegment computeLineIntersection(Line line);

    /**
     * Given a closed convex body K and a point x not in K, there is at least one hyperplane separating the point x from K
     * (i.e. the hyperplane is such that K is contained on one side of the hyperplane and x is on the other side). Computing
     * this hyperplane is used for the "rounding" optimization of the Hit-and-Run Sampler.
     *
     * In addition, note that there is a contract between this method and the isInside() function. We must have that x is inside K
     * if, and only if, there ISN'T a separating hyperplane (i.e. this method returns Optional.empty()).
     *
     * @param x: a data point
     * @return a LinearClassifier instance which separates the point x from the convex body. If x is on the interior of
     * the convex body, Optional.empty() is returned instead.
     * @throws IllegalArgumentException if x.length and getDim() are different
     */
    Optional<LinearClassifier> getSeparatingHyperplane(double[] x);
}
