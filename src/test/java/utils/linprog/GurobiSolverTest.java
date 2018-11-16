package utils.linprog;

import org.junit.jupiter.api.BeforeEach;

class GurobiSolverTest extends LinearProgramSolverTest {
    @BeforeEach
    void setUp() {
        library = LinearProgramSolver.LIBRARY.GUROBI;
        solver = LinearProgramSolver.getSolver(library, dim);
    }
}
