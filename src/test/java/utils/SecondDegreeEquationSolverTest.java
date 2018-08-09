package utils;

import exceptions.SecondDegreeEquationSolverFailed;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecondDegreeEquationSolverTest {
    @Test
    void solve_leadingCoefficientIsZeroAndDeltaIsPositive_throwsException() {
        assertThrows(SecondDegreeEquationSolverFailed.class, () -> SecondDegreeEquationSolver.solve(0, 2, 1));
    }

    @Test
    void solve_leadingCoefficientIsNotZeroButDeltaIsZero_throwsException() {
        assertThrows(SecondDegreeEquationSolverFailed.class, () -> SecondDegreeEquationSolver.solve(1, 2, 4));
    }

    @Test
    void solve_leadingCoefficientIsNotZeroButDeltaIsNegative_throwsException() {
        assertThrows(SecondDegreeEquationSolverFailed.class, () -> SecondDegreeEquationSolver.solve(1, 2, 8));
    }

    @Test
    void solve_leadingCoefficientIsNotZeroAndDeltaIsPositive_ReturnsExpectedSolutions() {
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(-1, 2, 5);
        assertEquals(-1, solution.getFirst());
        assertEquals(5, solution.getSecond());
    }
}