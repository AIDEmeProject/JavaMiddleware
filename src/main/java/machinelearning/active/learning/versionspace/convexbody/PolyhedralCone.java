package machinelearning.active.learning.versionspace.convexbody;

import data.LabeledDataset;
import data.LabeledPoint;
import explore.user.UserLabel;
import machinelearning.classifier.margin.LinearClassifier;
import utils.SecondDegreeEquationSolver;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;
import java.util.Optional;

/**
 * A polyhedral cone is a convex body defined by a set of homogeneous linear equations:
 *
 *      \( {x : \langle x, w_i \rangle \geq 0} \)
 *
 * @see <a href="https://en.wikipedia.org/wiki/Convex_cone">Convex Cone wiki</a>
 */
public class PolyhedralCone implements ConvexBody {
    /**
     * Collection of labeled points defining this polyhedral cone
     */
    private final LabeledDataset labeledPoints;

    /**
     * Factory instance of LP solvers, used for finding an interior point
     */
    private final LinearProgramSolver.FACTORY solverFactory;

    /**
     * @param labeledPoints: set of labeled points to built polytope. \(w_i = y_i x_i\)
     * @param solverFactory: factory instance for LP solvers
     */
    public PolyhedralCone(LabeledDataset labeledPoints, LinearProgramSolver.FACTORY solverFactory) {
        this.labeledPoints = labeledPoints;
        this.solverFactory = solverFactory;
    }

    public int getDim() {
        return labeledPoints.dim();
    }

    @Override
    public boolean isInside(Vector x) {
        return !getSeparatingHyperplane(x).isPresent();
    }

    /**
     * Finding an interior point to the convex cone is done by solving a linear program problem:
     *
     *  \(maximize_{x, s} s\), subject to \(\langle x, w_i \rangle \leq s, -1 \leq s, x_i \leq 1\)
     *
     * If the optimal solution \(s^*, x^*\) satisfies \(s^* &gt; 0\), then \(x^*\) is an interior point to the polytope.
     * The \(-1, 1\) constrains are just to bound the problem's solution.
     *
     * @return point interior to this polyhedral cone
     */
    // TODO: is there a mechanism to stop the LP solver once a s > 0 point has been found?
    @Override
    public Vector getInteriorPoint() {
        int dim = getDim();

        // configure linear program
        LinearProgramSolver solver = solverFactory.getSolver(dim + 1);

        double[] constrain = new double[dim + 1];
        constrain[0] = 1;
        solver.setObjectiveFunction(constrain);

        Vector y = Vector.FACTORY.make(
                Arrays.stream(labeledPoints.getLabels())
                        .mapToDouble(UserLabel::asSign)
                        .toArray()
        );
        Matrix X = labeledPoints.getData();
        Matrix A = X.multiplyColumn(y).addBiasColumn().iScalarMultiply(-1);  // a_i = (-1, -y_i x_i)

        for (int i = 0; i < A.rows(); i++) {
            solver.addLinearConstrain(A.getRow(i).toArray(), InequalitySign.LEQ, 0);  // -s - y_i <x_i, w> <= 0
        }

        constrain = new double[dim+1];
        Arrays.fill(constrain, -1);
        solver.setLower(constrain);

        constrain = new double[dim+1];
        Arrays.fill(constrain, 1);
        solver.setUpper(constrain);

        // find solution and return answer
        Vector solution = Vector.FACTORY.make(solver.findMinimizer());
        return solution.slice(1, solution.dim()).normalize(0.9);  // normalize point so it is contained on the unit ball
    }

    /**
     * Given a line \(C + t * D\), its intersection with a convex cone is defined as:
     *
     * \( \langle C + t * D, w_i \rangle \qeq 0 \)
     *
     * Which gives us the bounds:
     *
     * \(t_{min} = \max_{i : \langle D, w_i \rangle} < 0} - \frac{\langle C, w_i \rangle}{\langle D, w_i \rangle} \)
     * \(t_{max} = \min_{i : \langle D, w_i \rangle} > 0} - \frac{\langle C, w_i \rangle}{\langle D, w_i \rangle} \)
     *
     * @param line: straight line intersecting the convex body
     * @return line segment
     * @throws RuntimeException if line does not intercept the convex body
     */
    @Override
    public LineSegment computeLineIntersection(Line line) {
        Validator.assertEquals(getDim(), line.getDim());

        double leftBound = Double.NEGATIVE_INFINITY;
        double rightBound = Double.POSITIVE_INFINITY;

        // polytope intersection
        for (LabeledPoint point : labeledPoints){
            int signedLabel = point.getLabel().asSign();
            double numerator = signedLabel * point.getData().dot(line.getCenter());
            double denominator = signedLabel * point.getData().dot(line.getDirection());
            double value = - numerator / denominator;

            if (denominator > 0 && value > leftBound){
                leftBound = value;
            } else if (denominator < 0 && value < rightBound){
                rightBound = value;
            } else if (denominator == 0 && numerator <= 0 || leftBound >= rightBound){
                throw new RuntimeException("Line does not intercept convex body.");
            }
        }

        // ball intersection
        double a = line.getDirection().squaredNorm();
        double b = line.getCenter().dot(line.getDirection());
        double c = line.getCenter().squaredNorm() - 1;
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(a, b, c);
        leftBound = Math.max(leftBound, solution.getFirst());
        rightBound = Math.min(rightBound, solution.getSecond());

        if (leftBound >= rightBound){
            throw new RuntimeException("Line does not intercept convex body: " + leftBound + ", " + rightBound);
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
    @Override
    public Optional<LinearClassifier> getSeparatingHyperplane(Vector x) {
        Validator.assertEquals(x.dim(), getDim());

        if (x.squaredNorm() > 1) {
            return Optional.of(new LinearClassifier(-1, x.normalize(1.0)));  // return -1 for bias?
        }

        for (LabeledPoint point : labeledPoints) {
            if (point.getLabel().asSign() * point.getData().dot(x) < 0){
                Vector weights = point.getData().scalarMultiply(-point.getLabel().asSign());
                return Optional.of(new LinearClassifier(0, weights));
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return labeledPoints.toString();
    }
}
