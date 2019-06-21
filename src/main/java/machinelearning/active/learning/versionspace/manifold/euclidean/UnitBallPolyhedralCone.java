package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import utils.SecondDegreeEquationSolver;
import utils.linalg.Vector;

public class UnitBallPolyhedralCone implements ConvexBody {
    private PolyhedralCone cone;

    public UnitBallPolyhedralCone(PolyhedralCone cone) {
        this.cone = cone;
    }

    @Override
    public int dim() {
        return cone.dim();
    }

    @Override
    public boolean isInside(Vector point) {
        return point.squaredNorm() <= 1 && cone.isInside(point);
    }

    @Override
    public Vector getInteriorPoint() {
        return cone.getInteriorPoint().iNormalize(0.9);
    }

    @Override
    public GeodesicSegment computeIntersection(Geodesic line) {
        double a = line.getVelocity().squaredNorm();
        double b = line.getCenter().dot(line.getVelocity());
        double c = line.getCenter().squaredNorm() - 1;
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(a, b, c);

        return cone.computeIntersection(line, solution.getFirst(), solution.getSecond());
    }

    @Override
    public Manifold getManifold() {
        return cone.getManifold();
    }
}
