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
        APACHE, OJALGO, GUROBI
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
