package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.classifier.margin.HyperPlane;
import utils.SecondDegreeEquationSolver;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.LinearProgramSolver;

import java.util.Objects;

public class UnitBallPolyhedralCone implements EuclideanConvexBody {
    private PolyhedralCone cone;

    public UnitBallPolyhedralCone(PolyhedralCone cone) {
        this.cone = cone;
    }

    public UnitBallPolyhedralCone(Matrix A, LinearProgramSolver.FACTORY solver) {
        this.cone = new PolyhedralCone(A, solver);
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
    public double getRadius() {
        return 1.0;
    }

    @Override
    public double findInteriorEllipsoidParallelTo(Ellipsoid ellipsoid) {
        return 0;
    }

    @Override
    public HyperPlane getSeparatingHyperplane(Vector point) {
        return null;
    }

    @Override
    public Manifold getManifold() {
        return cone.getManifold();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitBallPolyhedralCone that = (UnitBallPolyhedralCone) o;
        return Objects.equals(cone, that.cone);
    }
}
