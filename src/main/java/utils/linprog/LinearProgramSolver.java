package utils.linprog;

import utils.Validator;

/**
 * Wrapper of most common Linear Programming (LP) solvers available in java. A LP refers to the problem:
 *
 * \[
 * \min_{x \in \mathbb{R}^d} \langle C, x \rangle, \text{ s.t. } Ax \leq b \text{ and } L \leq x \leq U
 * \]
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
     * Factory method for retrieving a given LP solver through its library name.
     * @param library: LP solver library
     * @param dim: expected dimension of each vector constrain
     * @return LP solver instance
     */
    static LinearProgramSolver getSolver(LinearProgramLibrary library, int dim){
        switch(library) { 
            case APACHE:
                return new ApacheLinearProgramSolver(dim);
            case OJALGO:
                return new OjalgoLinearProgramSolver(dim);
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Sets the C vector in the objective function \(\langle C, X \rangle \)
     * @param vector: C vector
     * @throws IllegalArgumentException if vectors's dimension is different from getDim().
     */
    void setObjectiveFunction(double[] vector);

    /**
     * Sets the lower bound L for each variable.
     * @param vector: vector whose i-th component specify the constrain \( L_i \leq x_i \)
     * @throws IllegalArgumentException if vector's dimension is different from getDim().
     */
    void setLower(double[] vector);

    /**
     * Sets the upper bound U for each variable.
     * @param vector: vector whose i-th component specify the constrain \( x_i \leq U_i \)
     * @throws IllegalArgumentException if vector's dimension is different from getDim().
     */
    void setUpper(double[] vector);

    /**
     * Computes the solution \( X^* \) of the LP
     * @return solver's solution for the LP
     * @throws RuntimeException if LP solver failed to find a solution
     */
    double[] findMinimizer();

    /**
     * Adds a new linear inequality constrain to the LP, given by \( \langle vector, x \rangle \leq val \)
     * @param vector: vector in LHS of inequality
     * @param val: RHS of inequality
     * @throws IllegalArgumentException if vector's dimension is different from getDim().
     */
    void addLinearConstrain(double[] vector, Relation relation, double val);
}
