package utils.linprog;

import org.apache.commons.math3.optim.linear.*;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Wrapper for Linear Programming solver in the Apache Commons Math library.
 * @see <a href="https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html?overview-summary.html">
 *     Apache Commons Math 3 linear optimization javadoc</a>
 * @author lucianodp
 */
public class ApacheLinearProgramSolver implements LinearProgramSolver {
    private final int dim;
    private LinearObjectiveFunction objective;
    private final Collection<LinearConstraint> constraints;

    ApacheLinearProgramSolver(int dim) {
        this.dim = dim;
        this.constraints = new ArrayList<>();
    }

    @Override
    public void setObjectiveFunction(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        objective = new LinearObjectiveFunction(vector, 0);
    }

    @Override
    public void addLinearConstrain(double[] vector, InequalitySign inequalitySign, double val) {
        Validator.assertEquals(vector.length, dim);
        constraints.add(new LinearConstraint(vector, inequalitySign == InequalitySign.LEQ ? Relationship.LEQ : Relationship.GEQ, val));
    }

    @Override
    public void setLower(double[] lowerBounds) {
        Validator.assertEquals(lowerBounds.length, dim);
        for (int i = 0; i < dim; i++) {
            double[] constrain = new double[dim];
            constrain[i] = 1;
            constraints.add(new LinearConstraint(constrain, Relationship.GEQ, lowerBounds[i]));
        }
    }

    @Override
    public void setUpper(double[] upperBounds) {
        Validator.assertEquals(upperBounds.length, dim);
        for (int i = 0; i < dim; i++) {
            double[] constrain = new double[dim];
            constrain[i] = 1;
            constraints.add(new LinearConstraint(constrain, Relationship.LEQ, upperBounds[i]));
        }
    }

    @Override
    public double[] findMinimizer() {
        try {
            return new SimplexSolver().optimize(objective, new LinearConstraintSet(constraints)).getPoint();

        } catch (NoFeasibleSolutionException ex){
            throw new exceptions.NoFeasibleSolutionException();
        }
        catch (UnboundedSolutionException ex){
            throw new exceptions.UnboundedSolutionException();
        }
    }
}
