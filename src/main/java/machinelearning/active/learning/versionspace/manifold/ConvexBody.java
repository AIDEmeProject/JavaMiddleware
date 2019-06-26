package machinelearning.active.learning.versionspace.manifold;

import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import utils.linalg.Vector;

public interface ConvexBody {
    int dim();

    boolean isInside(Vector point);

    Vector getInteriorPoint();

    GeodesicSegment computeIntersection(Geodesic geodesic);

    Manifold getManifold();

    /**
     * Given an ellipsoid containing {@code this} convex body, we attempt to reduce it i.e. construct a smaller ellipsoid
     * containing {@code this}.
     *
     * @param ellipsoid: an ellipsoid containing {@code this} convex body.
     * @return whether the ellipsoid could be reduced
     */
    boolean attemptToReduceEllipsoid(Ellipsoid ellipsoid);

    /**
     * @return an {@link Ellipsoid} instance containing this object
     */
    Ellipsoid getContainingEllipsoid();
}
