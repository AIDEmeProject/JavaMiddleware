package utils.linprog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class LinearProgramSolverTest {
    int dim = 2;
    LinearProgramSolver solver;

    @Test
    void setObjectiveFunction_ConstrainWithWrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> solver.setObjectiveFunction(new double[dim+1]));
    }

    @Test
    void addLinearConstrain_ConstrainWithWrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> solver.addLinearConstrain(new double[dim+1], InequalitySign.LEQ, 0));
    }

    @Test
    void setLower_ConstrainWithWrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> solver.setLower(new double[dim+1]));
    }

    @Test
    void setUpper_ConstrainWithWrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> solver.setUpper(new double[dim+1]));
    }

    @Test
    void testSolverOnProblem1() {
        testSolver(new double[] {1,1}, new double[][] {{-1,0}, {0,-1}, {1,1}}, new double[] {0,0,1}, new double[] {0,0});
    }

    @Test
    void testSolverOnProblem2() {
        testSolver(new double[] {-1,1}, new double[][] {{-1,0}, {0,-1}, {1,1}}, new double[] {0,0,1}, new double[] {1,0});
    }

    @Test
    void testSolverOnProblem3() {
        testSolver(new double[] {0,-1}, new double[][] {{-1,0}, {0,-1}, {1,1}}, new double[] {0,0,1}, new double[] {0,1});
    }

    private void testSolver(double[] objective, double[][] constrainsMatrix, double[] constrainsVector, double[] answer){
        solver.setObjectiveFunction(objective);
        for(int i=0; i < constrainsMatrix.length; i++) {
            solver.addLinearConstrain(constrainsMatrix[i], InequalitySign.LEQ, constrainsVector[i]);
        }
        double[] solution = solver.findMinimizer();
        assertArrayEquals(answer, solution, 1e-10);
    }

    // TODO: check behavior on unfeasible problems
}
