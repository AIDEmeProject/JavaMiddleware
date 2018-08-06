package utils.versionspace;

import classifier.linear.LinearClassifier;
import data.LabeledPoint;
import sampling.HitAndRunSampler;
import utils.SecondDegreeEquationSolver;
import utils.Validator;
import utils.convexbody.ConvexBody;
import utils.convexbody.Line;
import utils.convexbody.LineSegment;
import utils.linalg.LinearAlgebra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class LinearVersionSpace extends VersionSpace implements ConvexBody {
    private boolean hasBias;
    private HitAndRunSampler sampler;
    private Collection<double[]> sampleCache;
    private int dim;

    public LinearVersionSpace(HitAndRunSampler sampler, int dim, boolean addBias) {
        super();

        Validator.assertNotNull(sampler);
        Validator.assertPositive(dim);
        this.dim = dim;
        this.sampler = sampler;
        this.hasBias = addBias;
        this.sampleCache = new LinkedList<>();
    }

    @Override
    public void setLabeledPoints(Collection<LabeledPoint> points) {
        labeledPoints = new ArrayList<>(points.size());
        for (LabeledPoint point : points){
            labeledPoints.add(hasBias ? point.addBias() : point);
        }

        Validator.assertEquals(super.getDim(), dim);

        sampleCache.removeIf(cachedSample -> !isInside(cachedSample));
    }

    @Override
    public int getDim() {
        return hasBias ? dim + 1 : dim;
    }

    @Override
    public boolean isInside(double[] x) {
        Validator.assertEquals(x.length, getDim());

        for (LabeledPoint point : labeledPoints){
            int signedLabel = point.getLabel() == 1 ? 1 : -1;
            if (signedLabel * LinearAlgebra.dot(x, point.getData()) < 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public double[] getInteriorPoint() {
        if (!sampleCache.isEmpty()){
            return sampleCache.iterator().next();
        }

        //TODO: implement fallback Linear Programming method
        return new double[getDim()];
    }

    @Override
    public LineSegment computeLineIntersection(Line line) {
        Validator.assertEquals(line.getDim(), getDim());

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
        double a = LinearAlgebra.sqNorm(line.getCenter());
        double b = LinearAlgebra.dot(line.getCenter(), line.getDirection());
        double c = LinearAlgebra.sqNorm(line.getDirection()) - 1;
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(a, b, c);
        leftBound = Math.max(leftBound, solution.getLeft());
        rightBound = Math.min(rightBound, solution.getRight());

        if (leftBound >= rightBound){
            throw new RuntimeException("Line does not intercept convex body.");
        }

        return new LineSegment(line, leftBound, rightBound);
    }

    @Override
    public LinearClassifier[] sample(int numSamples){
        sampleCache.clear();
        LinearClassifier[] classifiers = new LinearClassifier[numSamples];

        int i = 0;
        for (double[] sampledWeight : sampler.sample(this, numSamples)) {
            classifiers[i++] = new LinearClassifier(sampledWeight, hasBias);
            sampleCache.add(sampledWeight);
        }

        return classifiers;
    }

}
