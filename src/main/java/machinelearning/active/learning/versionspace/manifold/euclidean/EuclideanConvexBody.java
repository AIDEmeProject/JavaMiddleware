package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.classifier.margin.HyperPlane;
import utils.linalg.EigenvalueDecomposition;
import utils.linalg.Vector;

public interface EuclideanConvexBody extends ConvexBody {
    @Override
    default Manifold getManifold() {
        return EuclideanSpace.getInstance();
    }

    /**
     * @return the radius R of any ball containing {@code this} convex body
     */
    double getRadius();

    /**
     * Computes a separating hyperplane between an data point x and {@code this} convex body. The point x is guaranteed
     * to have a positive margin, while any point on {@code this} convex body will have a non-positive margin.
     *
     * @param x: a point
     * @return a separating hyperplane
     * @throws RuntimeException if point is inside polytope
     */
    HyperPlane getSeparatingHyperplane(Vector x);

    /**
     * Given an ellipsoid containing {@code this} convex body, we attempt to reduce it i.e. construct a smaller ellipsoid
     * containing {@code this}.
     *
     * @param ellipsoid: an ellipsoid containing {@code this} convex body.
     * @return whether the ellipsoid could be reduced
     */
    default boolean attemptToReduceEllipsoid(Ellipsoid ellipsoid) {
        Vector center = ellipsoid.getCenter();
        if (!isInside(center) && ellipsoid.cut(getSeparatingHyperplane(center)))
            return true;

        EigenvalueDecomposition decomposition = new EigenvalueDecomposition(ellipsoid.getScale());

        for (int i = 0; i < dim(); i++) {
            double factor = Math.sqrt(decomposition.getEigenvalue(i)) / (dim() + 1.);
            Vector direction = decomposition.getEigenvector(i).iScalarMultiply(factor);

            Vector extreme = center.add(direction);
            if (!isInside(extreme) && ellipsoid.cut(getSeparatingHyperplane(extreme)))
                return true;

            extreme = center.subtract(direction);
            if (!isInside(extreme) && ellipsoid.cut(getSeparatingHyperplane(extreme)))
                return true;
        }

        return false;
    }
}
