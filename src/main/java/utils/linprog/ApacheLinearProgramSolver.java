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
