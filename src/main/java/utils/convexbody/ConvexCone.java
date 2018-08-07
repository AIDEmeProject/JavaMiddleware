package utils.convexbody;

import data.LabeledPoint;
import utils.SecondDegreeEquationSolver;
import utils.Validator;
import utils.linalg.LinearAlgebra;
import utils.linprog.LinearProgramLibrary;
import utils.linprog.LinearProgramSolver;
import utils.linprog.Relation;

import java.util.Arrays;
import java.util.Collection;

public class ConvexCone implements ConvexBody {
    private final Collection<LabeledPoint> labeledPoints;

    public ConvexCone(Collection<LabeledPoint> labeledPoints) {
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

    @Override
    public double[] getInteriorPoint() {
        int dim = labeledPoints.iterator().next().getDim();
        LinearProgramSolver solver = LinearProgramSolver.getSolver(LinearProgramLibrary.OJALGO, dim+1);

        double[] constrain = new double[dim+1];
        constrain[0] = 1;
        solver.setObjectiveFunction(constrain);

        for (LabeledPoint labeledPoint : labeledPoints) {
            constrain = labeledPoint.addBias().getData();
            constrain[0] = labeledPoint.getLabel() == 1 ? 1 : -1;
            solver.addLinearConstrain(constrain, labeledPoint.getLabel() == 1 ? Relation.GEQ : Relation.LEQ, 0);
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
        double c = LinearAlgebra.sqNorm(line.getCenter()) - line.getDim(); // 1 before
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(a, b, c);
        leftBound = Math.max(leftBound, solution.getLeft());
        rightBound = Math.min(rightBound, solution.getRight());

        if (leftBound >= rightBound){
            throw new RuntimeException("Line does not intercept convex body: " + leftBound + ", " + rightBound);
        }

        return  line.getSegment(leftBound, rightBound);
    }
}
