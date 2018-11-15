package utils.linprog;

import exceptions.NoFeasibleSolutionException;
import exceptions.UnboundedSolutionException;
import gurobi.*;
import utils.Validator;

public class GurobiSolver implements LinearProgramSolver {

    private final int dim;
    private GRBVar[] variables;
    private GRBModel model;

    public GurobiSolver(int dim) {
        this.dim = dim;

        try {
            GRBEnv env = new GRBEnv();

            env.set(GRB.IntParam.OutputFlag, 0);  // disable console logging

            model = new GRBModel(env);

            // Create variables
            variables = new GRBVar[dim];
            for (int i = 0; i < dim; i++) {
                variables[i] = model.addVar(-GRB.INFINITY, GRB.INFINITY, 0, GRB.CONTINUOUS, "x" + i);
            }
        } catch (GRBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setObjectiveFunction(double[] vector) {
        Validator.assertEquals(vector.length, dim);

        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < dim; i++) {
            expr.addTerm(vector[i], variables[i]);
        }

        try {
            model.setObjective(expr, GRB.MINIMIZE);
        } catch (GRBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setLower(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        try {
            for (int i = 0; i < dim; i++) {
                variables[i].set(GRB.DoubleAttr.LB, vector[i]);
            }
        } catch (GRBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setUpper(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        try {
            for (int i = 0; i < dim; i++) {
                variables[i].set(GRB.DoubleAttr.UB, vector[i]);
            }
        } catch (GRBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public double[] findMinimizer() {
        try {
            model.optimize();

            int status = model.get(GRB.IntAttr.Status);

            if (status == GRB.Status.INFEASIBLE) {
                throw new NoFeasibleSolutionException();
            }

            if (status == GRB.Status.UNBOUNDED) {
                throw new UnboundedSolutionException();
            }

            return model.get(GRB.DoubleAttr.X, model.getVars());

        } catch (GRBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addLinearConstrain(double[] vector, InequalitySign inequalitySign, double val) {
        Validator.assertEquals(vector.length, dim);

        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < dim; i++) {
            expr.addTerm(vector[i], variables[i]);
        }

        try {
            model.addConstr(
                    expr,
                    inequalitySign == InequalitySign.LEQ ? GRB.LESS_EQUAL : GRB.GREATER_EQUAL,
                    val,
                    "c" + model.getConstrs().length
            );
        } catch (GRBException ex) {
            throw new RuntimeException(ex);
        }
    }
}
