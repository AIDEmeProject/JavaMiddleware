package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;

/**
 * A polyhedral cone is a convex body defined by a set of homogeneous linear equations:
 *
 *         {x: Ax >= 0}
 *
 * @see <a href="https://en.wikipedia.org/wiki/Convex_cone">Convex Cone wiki</a>
 */
public class PolyhedralCone implements EuclideanConvexBody {
    /**
     * Collection of labeled points defining this polyhedral cone
     */
    private final Matrix A;

    /**
     * Factory instance of LP solvers, used for finding an interior point
     */
    private final LinearProgramSolver.FACTORY solverFactory;

    /**
     * @param A: set of labeled points to built polytope. \(w_i = y_i x_i\)
     * @param solverFactory: factory instance for LP solvers
     */
    public PolyhedralCone(Matrix A, LinearProgramSolver.FACTORY solverFactory) {
        this.A = A;
        this.solverFactory = solverFactory;
    }

    public int dim() {
        return A.cols();
    }

    @Override
    public boolean isInside(Vector x) {
        for (int i = 0; i < A.rows(); i++) {
            if (A.getRow(i).dot(x) < 0) return false;
        }

        return true; //!getSeparatingHyperplane(x).isPresent();
    }

    /**
     * Finding an interior point to the convex cone is done by solving a linear program problem:
     *
     *       minimize s, subject to x^T a_i >= -s, -1 <= s, x_i <= 1
     *
     * If the optimal solution (s^*, x^*) satisfies \(s^* < 0\), then x^* is an interior point to the polytope.
     * The (-1, 1) constrains are just to bound the problem's solution.
     *
     * @return point interior to this polyhedral cone
     */
    // TODO: is there a mechanism to stop the LP solver once a s > 0 point has been found?
    @Override
    public Vector getInteriorPoint() {
        int dim = dim();

        // configure linear program
        LinearProgramSolver solver = solverFactory.getSolver(dim + 1);

        double[] constrain = new double[dim + 1];
        constrain[0] = 1;
        solver.setObjectiveFunction(constrain);

        //  s + <a_i, x> >= 0
        Matrix B = A.addBiasColumn();  // b_i = (1, a_i)

        for (int i = 0; i < B.rows(); i++) {
            solver.addLinearConstrain(B.getRow(i).toArray(), InequalitySign.GEQ, 0);
        }

        // s, x_i >= -1
        constrain = new double[dim + 1];
        Arrays.fill(constrain, -1);
        solver.setLower(constrain);

        // s, x_i <= 1
        constrain = new double[dim + 1];
        Arrays.fill(constrain, 1);
        solver.setUpper(constrain);

        // find solution and return answer
        Vector solution = Vector.FACTORY.make(solver.findMinimizer());
        return solution.slice(1, solution.dim());
    }

    /**
     * @param line: straight line intersecting the convex body
     * @return the line segment resulting of the intersection between the line and this cone
     * @throws RuntimeException if the segment is empty (no intersection) or unbounded
     */
    @Override
    public GeodesicSegment computeIntersection(Geodesic line) {
        Validator.assertEquals(dim(), line.getDim());

        double leftBound = Double.NEGATIVE_INFINITY;
        double rightBound = Double.POSITIVE_INFINITY;

        Vector numerator = A.multiply(line.getCenter());
        Vector denominator = A.multiply(line.getVelocity());

        // polytope intersection
        for (int i = 0; i < numerator.dim(); i++) {
            double num = numerator.get(i);
            double den = denominator.get(i);
            double value = -num / den;

            if (den > 0 && value > leftBound) {
                leftBound = value;
            } else if (den < 0 && value < rightBound) {
                rightBound = value;
            } else if (den == 0 && num <= 0) {
                throw new RuntimeException("Line does not intercept convex body.");
            }
        }

        return line.getSegment(leftBound, rightBound);
    }

    /**
     * For a point x outside the polyhedral cone, we compute an separating hyperplane by using the following rule:
     *
     *      - if point is outside the unit ball, just return x itself
     *      - else, we look for a unsatisfying constraint \( y_i \langle x_i, x \rangle < 0 \), where we return \( -y_i x_i \)
     *
     * @param x: a data point
     * @return the separating hyperplane vector (if it exists)
     */
//    @Override
//    public Optional<LinearClassifier> getSeparatingHyperplane(Vector x) {
//        Validator.assertEquals(x.dim(), dim());
//
//        if (x.squaredNorm() > 1) {
//            return Optional.of(new LinearClassifier(-1, x.normalize(1.0)));  // return -1 for bias?
//        }
//
//        for (LabeledPoint point : A) {
//            if (point.getLabel().asSign() * point.getData().dot(x) < 0){
//                Vector weights = point.getData().scalarMultiply(-point.getLabel().asSign());
//                return Optional.of(new LinearClassifier(0, weights));
//            }
//        }
//
//        return Optional.empty();
//    }
}

