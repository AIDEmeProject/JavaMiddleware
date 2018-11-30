package utils.linprog;

import exceptions.UnboundedSolutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.linprog.InequalitySign.LEQ;

abstract class LinearProgramSolverTest {
    int dim = 2;
    LinearProgramSolver.LIBRARY library;
    LinearProgramSolver solver;

    @Test
    void getSolver_NegativeDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearProgramSolver.getSolver(library, -1));
    }

    @Test
    void getSolver_ZeroDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearProgramSolver.getSolver(library, 0));
    }

    @Test
    void setObjectiveFunction_ConstrainWithWrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> solver.setObjectiveFunction(new double[dim+1]));
    }

    @Test
    void addLinearConstrain_ConstrainWithWrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> solver.addLinearConstrain(new double[dim+1], LEQ, 0));
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
    void findMinimizer_noConstrainsAddedToSolver_throwsUnboundedSolutionException() {
        solver.setObjectiveFunction(new double[] {1,1});
        assertThrows(UnboundedSolutionException.class, () -> solver.findMinimizer());
    }

    /*
     * minimize x + y, s.t. x >= 1 , y >= 1, x <= 0, y <= 0
     * No solutions exist!
     * TODO: Ojalgo solver fails this test for unknown reason. Fix this!
     */
//    @Test
//    void findMinimizer_EmptyFeasibleRegion_ThrowsException() {
//        solver.setObjectiveFunction(new double[] {1,1});
//        solver.setLower(new double[] {1, 1});
//        solver.setUpper(new double[] {0, 0});
//        assertThrows(NoFeasibleSolutionException.class, () -> solver.findMinimizer());
//    }

    /*
     * minimize x, x <= 0, y <= 0, x >= 0, y >= 0
     * Only a single point is feasible: (0,0)
     */
    @Test
    void findMinimizer_FeasibleRegionConsistsOfSinglePoint_ThrowsException() {
        solver.setLower(new double[] {0, 0});
        solver.setUpper(new double[] {0, 0});
        assertSolverSolution(new double[] {0,0}, new double[] {1,1});
    }

    /*
     * minimize x, x <= 0, y <= 0
     * Unbounded solution (-infinity)
     */
    @Test
    void findMinimizer_UnboundedFeasibleRegionAndSolution_ThrowsException() {
        solver.setObjectiveFunction(new double[] {1,0});
        solver.setUpper(new double[] {0, 0});
        assertThrows(UnboundedSolutionException.class, () -> solver.findMinimizer());
    }

    /*
     * minimize x + y, s.t. x >= 0 , y >= 0
     * Feasible region is unbounded, but there is a solution at (0,0)
     */
    @Test
    void findMinimizer_UnboundedFeasibleRegionButBoundedSolution_CorrectSolutionComputed() {
        solver.setLower(new double[] {0, 0});
        solver.addLinearConstrain(new double[2], LEQ, 0);
        assertSolverSolution(new double[] {0, 0}, new double[] {1, 1});
    }

    /*
     * minimize a*x + b*y, s.t. 0.2 <= x <= 0.6, 0.3 <= y <= 0.7, 0.6 <= x + y <= 1
     * we choose (a,b) s.t. each time the solution falls in a different vertex of the feasible region polytope
     */
    @Test
    void findMinimizer_NonEmptyAndBoundedFeasibleRegion_MinimizerCorrectlyComputed() {
        solver.setLower(new double[] {0.2, 0.3});
        solver.setUpper(new double[] {0.6, 0.7});
        solver.addLinearConstrain(new double[] {1, 1}, LEQ, 1);
        solver.addLinearConstrain(new double[] {1, 1}, InequalitySign.GEQ, 0.6);
        assertSolverSolution(new double[] {0.2, 0.4}, new double[] {1.1, 0.9});
        assertSolverSolution(new double[] {0.3, 0.3}, new double[] {0.9, 1.1});
        assertSolverSolution(new double[] {0.6, 0.3}, new double[] {-1, 1});
        assertSolverSolution(new double[] {0.6, 0.4}, new double[] {-1.1, -0.9});
        assertSolverSolution(new double[] {0.3, 0.7}, new double[] {-0.9, -1.1});
        assertSolverSolution(new double[] {0.2, 0.7}, new double[] {1, -1});
    }

    private void assertSolverSolution(double[] answer, double[] objective){
        solver.setObjectiveFunction(objective);
        assertArrayEquals(answer, solver.findMinimizer(), 1e-10);
    }
}
