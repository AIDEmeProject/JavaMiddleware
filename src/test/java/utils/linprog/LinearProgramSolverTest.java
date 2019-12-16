/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
