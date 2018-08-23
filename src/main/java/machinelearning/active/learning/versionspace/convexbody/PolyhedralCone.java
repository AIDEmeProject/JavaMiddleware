package machinelearning.active.learning.versionspace.convexbody;

import data.LabeledPoint;
import utils.SecondDegreeEquationSolver;
import utils.Validator;
import utils.linalg.LinearAlgebra;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
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
    private final Collection<LabeledPoint> labeledPoints;

    /**
     * Factory instance of LP solvers, used for finding an interior point
     */
    private final LinearProgramSolver.FACTORY solverFactory;

    /**
     * @param labeledPoints: set of labeled points to built polytope. \(w_i = y_i x_i\)
     * @param solverFactory: factory instance for LP solvers
     */
    public PolyhedralCone(Collection<LabeledPoint> labeledPoints, LinearProgramSolver.FACTORY solverFactory) {
        Validator.assertNotEmpty(labeledPoints);
        this.labeledPoints = labeledPoints;
        this.solverFactory = solverFactory;
    }

    public int getDim() {
        return labeledPoints.iterator().next().getDim();
    }

    @Override
    public boolean isInside(double[] x) {
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
    public double[] getInteriorPoint() {
        LinearProgramSolver solver = solverFactory.getSolver(getDim()+1);
        configureLinearProgrammingProblem(solver);

        double[] interiorPoint = parseLinearProgramSolution(solver.findMinimizer());

        return LinearAlgebra.normalize(interiorPoint, 0.9);  // normalize point so it is contained on the unit ball
    }

    private void configureLinearProgrammingProblem(LinearProgramSolver solver) {
        int dim = getDim();

        double[] constrain = new double[dim+1];
        constrain[0] = 1;
        solver.setObjectiveFunction(constrain);

        for (LabeledPoint labeledPoint : labeledPoints) {
            constrain = labeledPoint.addBias().getData();
            constrain[0] = labeledPoint.getLabel().asSign();
            solver.addLinearConstrain(constrain, labeledPoint.getLabel().isPositive() ? InequalitySign.GEQ : InequalitySign.LEQ, 0);
        }

        constrain = new double[dim+1];
        Arrays.fill(constrain, -1);
        solver.setLower(constrain);

        constrain = new double[dim+1];
        Arrays.fill(constrain, 1);
        solver.setUpper(constrain);
    }

    private double[] parseLinearProgramSolution(double[] solution) {
        double[] optimalX = new double[solution.length-1];
        System.arraycopy(solution, 1, optimalX, 0, optimalX.length);
        return optimalX;
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
            double numerator = signedLabel * LinearAlgebra.dot(point.getData(), line.getCenter());
            double denominator = signedLabel * LinearAlgebra.dot(point.getData(), line.getDirection());
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
        double a = LinearAlgebra.sqNorm(line.getDirection());
        double b = LinearAlgebra.dot(line.getCenter(), line.getDirection());
        double c = LinearAlgebra.sqNorm(line.getCenter()) - 1;  //line.getDim();
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
    public Optional<double[]> getSeparatingHyperplane(double[] x) {
        Validator.assertEquals(x.length, getDim());

        if (LinearAlgebra.sqNorm(x) > 1) {
            return Optional.of(x);
        }

        for (LabeledPoint point : labeledPoints) {
            if (point.getLabel().asSign() * LinearAlgebra.dot(x, point.getData()) < 0){
                return Optional.of(LinearAlgebra.multiply(point.getData(), -point.getLabel().asSign()));
            }
        }

        return Optional.empty();
    }

    /**
     * @return true if both objects are PolyhedralCone instances containing the same labeled data.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolyhedralCone that = (PolyhedralCone) o;
        return Objects.equals(labeledPoints, that.labeledPoints);
    }
}
