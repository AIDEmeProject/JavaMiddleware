package utils.linprog;

import org.junit.jupiter.api.BeforeEach;

class ApacheLinearProgramSolverTest extends LinearProgramSolverTest {
    @BeforeEach
    void setUp() {
        solver = LinearProgramSolver.getSolver(LinearProgramLibrary.APACHE, dim);
    }
}
