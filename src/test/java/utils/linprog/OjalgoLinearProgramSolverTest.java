package utils.linprog;

import org.junit.jupiter.api.BeforeEach;

class OjalgoLinearProgramSolverTest extends LinearProgramSolverTest {
    @BeforeEach
    void setUp() {
        library = LinearProgramSolver.LIBRARY.OJALGO;
        solver = LinearProgramSolver.getSolver(library, dim);
    }
}
