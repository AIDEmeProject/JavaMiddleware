package machinelearning.active.learning.versionspace.manifold.cache;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import utils.linalg.Vector;


class ConvexBodyWrapper implements ConvexBody {
    private ConvexBody convexBody;
    private Vector interiorPointCache;
    private Ellipsoid ellipsoidCache;

    ConvexBodyWrapper(ConvexBody convexBody) {
        this.convexBody = convexBody;
        interiorPointCache = null;
        ellipsoidCache = null;
    }

    public void setInteriorPointCache(Vector interiorPointCache) {
        this.interiorPointCache = interiorPointCache;
    }

    public void setEllipsoidCache(Ellipsoid ellipsoidCache) {
        this.ellipsoidCache = ellipsoidCache;
    }

    @Override
    public int dim() {
        return convexBody.dim();
    }

    @Override
    public boolean isInside(Vector x) {
        return convexBody.isInside(x);
    }

    @Override
    public Vector getInteriorPoint() {
        return interiorPointCache != null ? interiorPointCache : convexBody.getInteriorPoint();
    }

    @Override
    public GeodesicSegment computeIntersection(Geodesic geodesic) {
        return convexBody.computeIntersection(geodesic);
    }

    @Override
    public Manifold getManifold() {
        return convexBody.getManifold();
    }

    @Override
    public boolean attemptToReduceEllipsoid(Ellipsoid ellipsoid) {
        return convexBody.attemptToReduceEllipsoid(ellipsoid);
    }

    @Override
    public Ellipsoid getContainingEllipsoid() {
        return ellipsoidCache != null ? ellipsoidCache : convexBody.getContainingEllipsoid();
    }
}
