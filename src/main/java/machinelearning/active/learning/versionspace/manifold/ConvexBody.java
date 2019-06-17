package machinelearning.active.learning.versionspace.manifold;

import utils.linalg.Vector;

public interface ConvexBody {
    int dim();

    boolean isInside(Vector point);

    Vector getInteriorPoint();

    GeodesicSegment computeIntersection(Geodesic geodesic);

    Manifold getManifold();
}
