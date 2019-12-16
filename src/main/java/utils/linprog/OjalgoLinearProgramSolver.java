/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package utils.linprog;

import exceptions.NoFeasibleSolutionException;
import exceptions.UnboundedSolutionException;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import utils.Validator;

/**
 * Wrapper for Linear Programming solver in the OjAlgo library.
 *
 * @see <a href="https://github.com/optimatika/ojAlgo/wiki/The-Diet-Problem">OjAlgo wiki on Linear Programming</a>
 * @author lucianodp
 */
public class OjalgoLinearProgramSolver implements LinearProgramSolver {
    private int numConstrains;
    private final int dim;
    private final ExpressionsBasedModel tmpModel;

    public OjalgoLinearProgramSolver(int dim){
        this.dim = dim;
        this.numConstrains = 0;
        this.tmpModel = new ExpressionsBasedModel();
        for (int i=0; i < dim; i++){
            Variable var = Variable.make(String.format("X%d", i+1));
            this.tmpModel.addVariable(var);
        }
    }

    @Override
    public void setObjectiveFunction(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        for(int i=0; i < dim; i++){
            tmpModel.getVariable(i).weight(vector[i]);
        }
    }

    @Override
    public void setLower(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        for(int i=0; i < dim; i++){
            tmpModel.getVariable(i).lower(vector[i]);
        }
    }

    @Override
    public void setUpper(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        for(int i=0; i < dim; i++){
            tmpModel.getVariable(i).upper(vector[i]);
        }
    }

    @Override
    public void addLinearConstrain(double[] vector, InequalitySign inequalitySign, double val) {
        Validator.assertEquals(vector.length, dim);

        numConstrains++;

        Expression exp = tmpModel.addExpression(String.format("constrain_%d", numConstrains));

        if (inequalitySign == InequalitySign.LEQ) {
            exp.upper(val);
        }
        else {
            exp.lower(val);
        }

        for(int i=0; i < dim; i++) {
            exp.set(tmpModel.getVariable(i), vector[i]);
        }
    }

    @Override
    public double[] findMinimizer() {
        Optimisation.Result minimizer = tmpModel.minimise();

        if (!minimizer.getState().isFeasible()){
            throw new NoFeasibleSolutionException();
        }
        if (minimizer.getState().isFailure()){
            throw new UnboundedSolutionException();
        }

        double[] res = new double[dim];
        for(int i=0; i < dim; i++)
            res[i] = minimizer.get(i).doubleValue();
        return res;
    }

}
