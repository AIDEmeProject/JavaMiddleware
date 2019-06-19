package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.euclidean.PolyhedralCone;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class UnitSpherePolyhedralCone implements ConvexBody {

    private final static Manifold sphere = new UnitSphere();
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
        if (!cone.isInside(geodesic.getCenter()))
            throw new RuntimeException("Geodesic center outside cone.");

        Matrix A = cone.getMatrix();

        Vector a = A.multiply(geodesic.getCenter());
        Vector b = A.multiply(geodesic.getVelocity());

        double lower = -Math.PI, upper = Math.PI;

        for (int i = 0; i < a.dim(); i++) {
            double angle = Math.atan2(-a.get(i), b.get(i));
            lower = Math.max(lower, angle);
            upper = Math.min(upper, angle + Math.PI);
        }

        if (lower >= upper)
            throw new RuntimeException("Empty intersection");

        return geodesic.getSegment(lower, upper);
    }

    @Override
    public Manifold getManifold() {
        return sphere;
    }
}
