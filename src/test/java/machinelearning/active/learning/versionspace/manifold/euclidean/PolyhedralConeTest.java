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

package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.classifier.margin.CenteredHyperPlane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class PolyhedralConeTest {
    private Matrix data;
    private PolyhedralCone cone;

    @BeforeEach
    void setUp() {
        data = Matrix.FACTORY.make(2, 2, 1, 0, 0, -1);
        cone = new PolyhedralCone(data, mock(LinearProgramSolver.FACTORY.class));
    }

    @Test
    void dim_twoDimensionalCone_returnsTwo() {
        assertEquals(2, cone.dim());
    }

    @Test
    void isInside_invalidDimensionInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> cone.isInside(Vector.FACTORY.zeros(1)));
    }

    @Test
    void isInside_pointOnInteriorOfCone_returnsTrue() {
        assertTrue(cone.isInside(Vector.FACTORY.make(0.5, -0.5)));
    }

    @Test
    void isInside_pointOnExteriorOfCone_returnsFalse() {
        assertFalse(cone.isInside(Vector.FACTORY.make(-1, 1)));
        assertFalse(cone.isInside(Vector.FACTORY.make(-1, -1)));
        assertFalse(cone.isInside(Vector.FACTORY.make(1, 1)));
    }

    @Test
    void isInside_pointOnBoundaryOfCone_returnsFalse() {
        assertFalse(cone.isInside(Vector.FACTORY.make(1, 0)));
        assertFalse(cone.isInside(Vector.FACTORY.make(0, -1)));
    }

    @Test
    void getInteriorPoint_computeInteriorPoint_solverFactoryCalledOnceWithCorrectParameter() {
        LinearProgramSolver solver = mock(LinearProgramSolver.class);
        when(solver.findMinimizer()).thenReturn(new double[] {-1, 0.5, -0.5});

        LinearProgramSolver.FACTORY solverFactory = mock(LinearProgramSolver.FACTORY.class);
        when(solverFactory.getSolver(anyInt())).thenReturn(solver);

        cone = new PolyhedralCone(data, solverFactory);
        cone.getInteriorPoint();

        verify(solverFactory).getSolver(3);
    }

    @Test
    void getInteriorPoint_coneComposedOfTwoConstraints_LinearProgramProblemCorrectlySetUp() {
        LinearProgramSolver solver = mock(LinearProgramSolver.class);
        when(solver.findMinimizer()).thenReturn(new double[] {-1, 0.5, -0.5});

        LinearProgramSolver.FACTORY solverFactory = mock(LinearProgramSolver.FACTORY.class);
        when(solverFactory.getSolver(anyInt())).thenReturn(solver);

        cone = new PolyhedralCone(data, solverFactory);
        cone.getInteriorPoint();

        verify(solver).setLower(new double[] {-1, -1, -1});  // x, y, s >= -1
        verify(solver).setUpper(new double[] {1, 1, 1});  // x, y, s <= 1
        verify(solver).setObjectiveFunction(new double[] {1, 0, 0});  // minimize s
        verify(solver).addLinearConstrain(new double[] {1, 1, 0}, InequalitySign.GEQ, 0);  // x >= 0 -> s + x >= 0
        verify(solver).addLinearConstrain(new double[] {1, 0, -1}, InequalitySign.GEQ, 0);  // y <= 0 -> s - y >= 0
        verify(solver).findMinimizer();  // findMinimizer() called once
    }

    @Test
    void getInteriorPoint_coneComposedOfTwoConstraints_finalSolutionCorrectlyParsed() {
        LinearProgramSolver solver = mock(LinearProgramSolver.class);
        when(solver.findMinimizer()).thenReturn(new double[]{-1, 0.5, -0.5});

        LinearProgramSolver.FACTORY solverFactory = mock(LinearProgramSolver.FACTORY.class);
        when(solverFactory.getSolver(anyInt())).thenReturn(solver);

        cone = new PolyhedralCone(data, solverFactory);
        Vector result = cone.getInteriorPoint();

        assertEquals(Vector.FACTORY.make(0.5, -0.5), result);
    }

    @Test
    void computeIntersection_lineOfWrongDimension_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(3), Vector.FACTORY.make(1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> cone.computeIntersection(line));
    }

    @Test
    void computeIntersection_lineDoesNotInterceptPolytope_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1, 1));
        assertThrows(RuntimeException.class, () -> cone.computeIntersection(line));
    }

    @Test
    void computeIntersection_lineContainsBoundaryOfPolytope_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1, 0));
        assertThrows(RuntimeException.class, () -> cone.computeIntersection(line));
    }

    @Test
    void computeIntersection_infiniteRay_throwsException() {
        Line line = new Line(Vector.FACTORY.make(0.5, -0.5), Vector.FACTORY.make(1, 0));
        assertThrows(RuntimeException.class, () -> cone.computeIntersection(line));
    }

    @Test
    void computeIntersection_boundedSegment_segmentCorrectlyComputed() {
        Line line = new Line(Vector.FACTORY.make(1, -1), Vector.FACTORY.make(1, 1));
        GeodesicSegment segment = cone.computeIntersection(line);
        assertEquals(-1, segment.getLowerBound());
        assertEquals(1, segment.getUpperBound());
    }

    @Test
    void getSeparatingHyperplane_PointOnInteriorOfCone_throwsException() {
        Vector x = Vector.FACTORY.make(1, -1);
        assertThrows(RuntimeException.class, () -> cone.getSeparatingHyperplane(x));
    }

    @Test
    void getSeparatingHyperplane_PointOnBoundaryOfCone_throwsException() {
        Vector x = Vector.FACTORY.make(1, 0);
        assertEquals(new CenteredHyperPlane(Vector.FACTORY.make(0, 1)), cone.getSeparatingHyperplane(x));
    }

    @Test
    void getSeparatingHyperplane_PointWithBothCoordinatesNegative_returnsXAxisConstraint() {
        Vector x = Vector.FACTORY.make(-1, -1);
        assertEquals(new CenteredHyperPlane(Vector.FACTORY.make(-1, 0)), cone.getSeparatingHyperplane(x));
    }
}