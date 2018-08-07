package utils.linprog;

import org.junit.jupiter.api.BeforeEach;

class OjalgoLinearProgramSolverTest extends LinearProgramSolverTest {
    @BeforeEach
    void setUp() {
        solver = LinearProgramSolver.getSolver(LinearProgramLibrary.OJALGO, dim);
    }
}
