package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.euclidean.PolyhedralCone;
import utils.linalg.Vector;

public class UnitSpherePolyhedralCone implements ConvexBody {
    private PolyhedralCone cone;

    public UnitSpherePolyhedralCone(PolyhedralCone cone) {
        this.cone = cone;
    }

    @Override
    public int dim() {
        return cone.dim();
    }

    @Override
    public boolean isInside(Vector point) {
        return point.squaredNorm() == 1 && cone.isInside(point);
    }

    @Override
    public Vector getInteriorPoint() {
        return cone.getInteriorPoint().iNormalize(1.0);
    }

    @Override
    public GeodesicSegment computeIntersection(Geodesic geodesic) {
        //TODO: implement this
        return null;
    }

    @Override
    public Manifold getManifold() {
        return cone.getManifold();
    }
}
