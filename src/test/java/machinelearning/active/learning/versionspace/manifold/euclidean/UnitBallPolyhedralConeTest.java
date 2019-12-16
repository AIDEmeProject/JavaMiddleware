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
import machinelearning.classifier.margin.HyperPlane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class UnitBallPolyhedralConeTest {
    private Matrix data;
    private PolyhedralCone cone;
    private UnitBallPolyhedralCone body;

    @BeforeEach
    void setUp() {
        data = Matrix.FACTORY.make(2, 2, 1, 0, 0, -1);
        cone = new PolyhedralCone(data, mock(LinearProgramSolver.FACTORY.class));
        body = new UnitBallPolyhedralCone(cone);
    }

    @Test
    void dim_twoDimensionalLabeledPoints_returnsTwo() {
        assertEquals(2, body.dim());
    }

    @Test
    void isInside_invalidDimensionInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> body.isInside(Vector.FACTORY.zeros(1)));
    }

    @Test
    void isInside_pointOnInterior_returnsTrue() {
        assertTrue(body.isInside(Vector.FACTORY.make(0.5, -0.5)));
    }

    @Test
    void isInside_pointOnExterior_returnsFalse() {
        assertFalse(body.isInside(Vector.FACTORY.make(-1, 1)));
        assertFalse(body.isInside(Vector.FACTORY.make(-1, -1)));
        assertFalse(body.isInside(Vector.FACTORY.make(1, 1)));
        assertFalse(body.isInside(Vector.FACTORY.make(1, -1)));
    }

    @Test
    void isInside_pointOnBoundary_returnsFalse() {
        assertFalse(body.isInside(Vector.FACTORY.make(1, 0)));
        assertFalse(body.isInside(Vector.FACTORY.make(0, -1)));
    }

    @Test
    void getInteriorPoint_mockPolyhedralCone_mockGetInteriorCalledOnce() {
        cone = mock(PolyhedralCone.class);
        when(cone.getInteriorPoint()).thenReturn(Vector.FACTORY.make(0.5, -0.5));

        body = new UnitBallPolyhedralCone(cone);
        body.getInteriorPoint();

        verify(cone).getInteriorPoint();
    }

    @Test
    void computeIntersection_lineOfWrongDimension_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(3), Vector.FACTORY.make(1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> body.computeIntersection(line));
    }

    @Test
    void computeIntersection_lineDoesNotInterceptPolytope_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1, 1));
        assertThrows(RuntimeException.class, () -> body.computeIntersection(line));
    }

    @Test
    void computeIntersection_lineContainsBoundaryOfPolytope_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1, 0));
        assertThrows(RuntimeException.class, () -> body.computeIntersection(line));
    }

    @Test
    void computeIntersection_lineIntersectsPolytope_lineSegmentCorrectlyComputed() {
        Line line = new Line(Vector.FACTORY.make(0.5, -0.5), Vector.FACTORY.make(1, 0));
        GeodesicSegment segment = body.computeIntersection(line);
        assertEquals(-0.5, segment.getLowerBound());
        assertEquals(Math.sqrt(0.75) - 0.5, segment.getUpperBound());
    }

    @Test
    void getSeparatingHyperplane_InputNormLargerThanOne_returnsHyperplanePerpendicularToInput() {
        Vector x = Vector.FACTORY.make(1, 1);
        HyperPlane hyperplane = body.getSeparatingHyperplane(x);
        assertEquals(new HyperPlane(-1, x.normalize(1.0)), hyperplane);
    }

    @Test
    void getSeparatingHyperplane_InputWithNegativeFirstCoordinateAndInsideUnitBall_returnsConesXAxisConstraint() {
        Vector x = Vector.FACTORY.make(-0.5, -0.5);
        HyperPlane hyperplane = body.getSeparatingHyperplane(x);
        assertEquals(new HyperPlane(0, Vector.FACTORY.make(-1, 0)), hyperplane);
    }

    @Test
    void getSeparatingHyperplane_InputWithPositiveFirstAndSecondCoordinatesAndInsideUnitBall_returnsConesYAxisConstraint() {
        Vector x = Vector.FACTORY.make(0.5, 0.5);
        HyperPlane hyperplane = body.getSeparatingHyperplane(x);
        assertEquals(new HyperPlane(0, Vector.FACTORY.make(0, 1)), hyperplane);
    }
}