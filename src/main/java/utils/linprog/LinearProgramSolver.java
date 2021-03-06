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

import utils.Validator;

/**
 * Wrapper of most common Linear Programming (LP) solvers available in java. A LP refers to the problem:
 *
 * \( \min_{x \in \mathbb{R}^d} \langle C, x \rangle, \text{ s.t. } Ax \leq b \text{ and } L \leq x \leq U \)
 *
 * The current interface does not support adding equality constrains. This can be added if needed in the future.
 *
 * We support two solver libraries: Ojalgo and Apache Commons Math. Note that the choice of LP Solver library is independent
 * from the choice of Linear Algebra library.
 *
 * @author lucianodp
 */
public interface LinearProgramSolver {
    /**
     * Supported linear programming libraries.
     *
     * @see ApacheLinearProgramSolver
     * @see OjalgoLinearProgramSolver
     */
    enum LIBRARY {
        APACHE, OJALGO
    }

    /**
     * Factory class for instantiating LP solvers from a particular library. This is used in order to decouple our code
     * from the chosen LP library.
     */
    class FACTORY {
        private LIBRARY library;

        private FACTORY(LIBRARY library) {
            this.library = library;
        }

        public LinearProgramSolver getSolver(int dim) {
            return LinearProgramSolver.getSolver(library, dim);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FACTORY factory = (FACTORY) o;
            return library == factory.library;
        }
    }

    /**
     * @param library LP library to use
     * @return a LinearProgrammingSolver factory which consistently returns solvers from the input library
     */
    static FACTORY getFactory(LIBRARY library) {
        return new FACTORY(library);
    }

    /**
     * @param library: LP solver library
     * @param dim: expected dimension of vector constraints
     * @return a LP solver from a given library
     */
    static LinearProgramSolver getSolver(LIBRARY library, int dim){
        Validator.assertPositive(dim);
        switch(library) { 
            case APACHE:
                return new ApacheLinearProgramSolver(dim);
            case OJALGO:
                return new OjalgoLinearProgramSolver(dim);
            default:
                throw new RuntimeException("Unknown Linear Program library: " + library);
        }
    }

    /**
     * Sets the C vector in the objective function \(\langle C, X \rangle \)
     * @param vector: C vector
     * @throws IllegalArgumentException if input has incompatible dimension.
     */
    void setObjectiveFunction(double[] vector);

    /**
     * Sets the lower bound L for each variable.
     * @param vector: vector whose i-th component specify the constrain \( x_i \geq L_i \)
     * @throws IllegalArgumentException  if input has incompatible dimension.
     */
    void setLower(double[] vector);

    /**
     * Sets the upper bound U for each variable.
     * @param vector: vector whose i-th component specify the constrain \( x_i \leq U_i \)
     * @throws IllegalArgumentException  if input has incompatible dimension.
     */
    void setUpper(double[] vector);

    /**
     * Computes the solution \( X^* \) of the LP
     * @return solver's solution for the LP
     * @throws RuntimeException if LP solver failed to find a solution
     */
    double[] findMinimizer();

    /**
     * Adds a new linear inequality constrain to the LP, defined by a weight vector, a inequalitySign (inequality sign), and a free value
     * @param vector: vector in LHS of inequality
     * @param inequalitySign: inequality sign
     * @param val: RHS of inequality
     * @throws IllegalArgumentException  if input has incompatible dimension.
     */
    void addLinearConstrain(double[] vector, InequalitySign inequalitySign, double val);
}
