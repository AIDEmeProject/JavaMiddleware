package utils.linprog;

import org.junit.jupiter.api.BeforeEach;

class ApacheLinearProgramSolverTest extends LinearProgramSolverTest {
    @BeforeEach
    void setUp() {
        library = LinearProgramSolver.LIBRARY.APACHE;
        solver = LinearProgramSolver.getSolver(library, dim);
    }
}
