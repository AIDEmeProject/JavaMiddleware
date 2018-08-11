package machinelearning.active.learning.versionspace.convexbody;

import data.LabeledPoint;
import utils.SecondDegreeEquationSolver;
import utils.Validator;
import utils.linalg.LinearAlgebra;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;
import java.util.Collection;

/**
 * A polyhedral cone is a convex body defined by a set of homogeneous linear equations:
 *
 *      \( {x : \langle x, w_i \rangle &gq; 0} \)
 *
 * @see <a href="https://en.wikipedia.org/wiki/Convex_cone">Convex Cone wiki</a>
 */
public class PolyhedralCone implements ConvexBody {
    private final Collection<LabeledPoint> labeledPoints;

    /**
     * @param labeledPoints: set of labeled points to built polytope. \(w_i = y_i x_i\)
     */
    public PolyhedralCone(Collection<LabeledPoint> labeledPoints) {
        Validator.assertNotEmpty(labeledPoints);
        this.labeledPoints = labeledPoints;
    }

    public int getDim() {
        return labeledPoints.iterator().next().getDim();
    }

    @Override
    public boolean isInside(double[] x) {
        Validator.assertEquals(x.length, getDim());

        for (LabeledPoint point : labeledPoints){
            int signedLabel = point.getLabel() == 1 ? 1 : -1;
            if (signedLabel * LinearAlgebra.dot(x, point.getData()) <= 0){
                return false;
            }
        }
        return true;
    }

    /**
     * Finding an interior point to the convex cone is done by solving a linear program problem:
     *
     *  \(maximize_{x, s} s\), subject to \(\langle x, w_i \rangle &gq; s, -1 \leq s, x_i \leq 1\)
     *
     * If optimal solution \(s^*, x^*\) satisfies \(s^* &gt; 0\), then \(x^*\) is an interior point to the polytope.
     * The \(-1, 1\) constrains are just to bound the problem's solution.
     *
     * @return point interior to this polyhedral cone
     */
    // TODO: is there a mechanism to stop the LP solver once a s > 0 point has been found?
    @Override
    public double[] getInteriorPoint() {
        int dim = getDim();
        LinearProgramSolver solver = LinearProgramSolver.getSolver(LinearProgramSolver.LIBRARY.OJALGO, dim+1);

        double[] constrain = new double[dim+1];
        constrain[0] = 1;
        solver.setObjectiveFunction(constrain);

        for (LabeledPoint labeledPoint : labeledPoints) {
            constrain = labeledPoint.addBias().getData();
            constrain[0] = labeledPoint.getLabel() == 1 ? 1 : -1;
            solver.addLinearConstrain(constrain, labeledPoint.getLabel() == 1 ? InequalitySign.GEQ : InequalitySign.LEQ, 0);
        }

        for (int i = 0; i < dim+1; i++) {
            constrain = new double[dim+1];
            Arrays.fill(constrain, -1);
            solver.setLower(constrain);

            constrain = new double[dim+1];
            Arrays.fill(constrain, 1);
            solver.setUpper(constrain);
        }


        double[] optimalX = new double[dim];
        System.arraycopy(solver.findMinimizer(), 1, optimalX, 0, dim);
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
        double leftBound = Double.NEGATIVE_INFINITY;
        double rightBound = Double.POSITIVE_INFINITY;

        // polytope intersection
        for (LabeledPoint point : labeledPoints){
            int signedLabel = point.getLabel() == 1 ? 1 : -1;
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
        double c = LinearAlgebra.sqNorm(line.getCenter()) - line.getDim();
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(a, b, c);
        leftBound = Math.max(leftBound, solution.getFirst());
        rightBound = Math.min(rightBound, solution.getSecond());

        if (leftBound >= rightBound){
            throw new RuntimeException("Line does not intercept convex body: " + leftBound + ", " + rightBound);
        }

        return  line.getSegment(leftBound, rightBound);
    }
}
