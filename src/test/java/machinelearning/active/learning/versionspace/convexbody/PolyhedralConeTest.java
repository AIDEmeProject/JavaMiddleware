package machinelearning.active.learning.versionspace.convexbody;

import data.LabeledPoint;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.LinearAlgebra;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class PolyhedralConeTest {
    private Collection<LabeledPoint> points;
    private PolyhedralCone cone;

    @BeforeEach
    void setUp() {
        points = new ArrayList<>();
        points.add(new LabeledPoint(0, new double[]{1, 0}, Label.POSITIVE));  // x >= 0
        points.add(new LabeledPoint(1, new double[]{0, 1}, Label.NEGATIVE));  // y <= 0
        cone = new PolyhedralCone(points, mock(LinearProgramSolver.FACTORY.class));
    }

    @Test
    void constructor_emptyLabeledPoints_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new PolyhedralCone(Collections.EMPTY_LIST, mock(LinearProgramSolver.FACTORY.class)));
    }

    @Test
    void getDim_twoDimensionalLabeledPoints_returnsTwo() {
        assertEquals(2, cone.getDim());
    }

    @Test
    void isInside_invalidDimensionInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> cone.isInside(new double[1]));
    }

    @Test
    void isInside_pointOnInteriorOfCone_returnsTrue() {
        assertTrue(cone.isInside(new double[] {0.5, -0.5}));
    }

    @Test
    void isInside_pointOnExteriorOfCone_returnsFalse() {
        assertFalse(cone.isInside(new double[] {-1, 1}));
        assertFalse(cone.isInside(new double[] {-1, -1}));
        assertFalse(cone.isInside(new double[] {1, 1}));
    }

    @Test
    void isInside_pointOnBoundaryOfCone_returnsTrue() {
        assertTrue(cone.isInside(new double[] {1, 0}));
        assertTrue(cone.isInside(new double[] {0, -1}));
    }

    @Test
    void getInteriorPoint_computeInteriorPoint_solverFactoryCalledOneWithCorrectParameter() {
        LinearProgramSolver solver = mock(LinearProgramSolver.class);
        when(solver.findMinimizer()).thenReturn(new double[] {-1, 1, -1});

        LinearProgramSolver.FACTORY solverFactory = mock(LinearProgramSolver.FACTORY.class);
        when(solverFactory.getSolver(anyInt())).thenReturn(solver);

        cone = new PolyhedralCone(points, solverFactory);
        cone.getInteriorPoint();

        verify(solverFactory).getSolver(3);
    }

    @Test
    void getInteriorPoint_coneComposedOfTwoConstraints_LinearProgramProblemCorrectlySetUp() {
        LinearProgramSolver solver = mock(LinearProgramSolver.class);
        when(solver.findMinimizer()).thenReturn(new double[] {-1, 1, -1});

        LinearProgramSolver.FACTORY solverFactory = mock(LinearProgramSolver.FACTORY.class);
        when(solverFactory.getSolver(anyInt())).thenReturn(solver);

        cone = new PolyhedralCone(points, solverFactory);
        cone.getInteriorPoint();

        verify(solver).setLower(new double[] {-1, -1, -1});  // x, y, s >= -1
        verify(solver).setUpper(new double[] {1, 1, 1});  // x, y, s <= 1
        verify(solver).setObjectiveFunction(new double[] {1, 0, 0});  // minimize s
        verify(solver).addLinearConstrain(new double[] {1, 1, 0}, InequalitySign.GEQ, 0);  // x >= -s -> s + x >= 0
        verify(solver).addLinearConstrain(new double[] {-1, 0, 1}, InequalitySign.LEQ, 0);  // y <= s -> -s + y <= 0
        verify(solver).findMinimizer();  // findMinimizer() called once
    }

    @Test
    void getInteriorPoint_coneComposedOfTwoConstraints_finalSolutionCorrectlyParsed() {
        LinearProgramSolver solver = mock(LinearProgramSolver.class);
        when(solver.findMinimizer()).thenReturn(new double[]{-1, 1, -1});

        LinearProgramSolver.FACTORY solverFactory = mock(LinearProgramSolver.FACTORY.class);
        when(solverFactory.getSolver(anyInt())).thenReturn(solver);

        cone = new PolyhedralCone(points, solverFactory);
        double[] result = cone.getInteriorPoint();

        assertArrayEquals(LinearAlgebra.normalize(new double[] {1, -1}, 0.9), result);
    }

    @Test
    void computeLineIntersection_lineOfWrongDimension_throwsException() {
        Line line = new Line(new double[]{0, 0, 0}, new double[]{1, 1, 1});
        assertThrows(IllegalArgumentException.class, () -> cone.computeLineIntersection(line));
    }

    @Test
    void computeLineIntersection_lineDoesNotInterceptPolytope_throwsException() {
        Line line = new Line(new double[]{0, 0}, new double[]{1, 1});
        assertThrows(RuntimeException.class, () -> cone.computeLineIntersection(line));
    }

    @Test
    void computeLineIntersection_lineContainsBoundaryOfPolytope_throwsException() {
        Line line = new Line(new double[]{0, 0}, new double[]{1, 0});
        assertThrows(RuntimeException.class, () -> cone.computeLineIntersection(line));
    }

    @Test
    void computeLineIntersection_lineIntersectsPolytope_lineSegmentCorrectlyComputed() {
        Line line = new Line(new double[]{0.5, -0.5}, new double[]{1, 0});
        LineSegment segment = cone.computeLineIntersection(line);
        assertEquals(-0.5, segment.getLeftBound());
        assertEquals(Math.sqrt(0.75) - 0.5, segment.getRightBound());
    }

    @Test
    void getSeparatingHyperplane_InputNormLargerThanOne_returnsHyperplanePerpendicularToInput() {
        double[] x = new double[]{1, 1};
        Optional<double[]> hyperplane = cone.getSeparatingHyperplane(x);
        assertTrue(hyperplane.isPresent());
        assertArrayEquals(x, hyperplane.get(), 1e-10);
    }

    @Test
    void getSeparatingHyperplane_InputOnInteriorOfCone_returnsEmptyOption() {
        double[] x = new double[]{0.5, -0.5};
        Optional<double[]> hyperplane = cone.getSeparatingHyperplane(x);
        assertFalse(hyperplane.isPresent());
    }

    @Test
    void getSeparatingHyperplane_InputOnBoundaryOfCone_returnsEmptyOption() {
        double[] x = new double[]{0.5, 0};
        Optional<double[]> hyperplane = cone.getSeparatingHyperplane(x);
        assertFalse(hyperplane.isPresent());
    }

    @Test
    void getSeparatingHyperplane_InputWithNegativeFirstCoordinateButInsideUnitBall_returnsConesXAxisConstraint() {
        double[] x = new double[]{-0.5, -0.5};
        Optional<double[]> hyperplane = cone.getSeparatingHyperplane(x);
        assertTrue(hyperplane.isPresent());
        assertArrayEquals(new double[] {-1, 0}, hyperplane.get(), 1e-10);
    }

    @Test
    void getSeparatingHyperplane_InputWithPositiveFirstAndSecondCoordinatesButInsideUnitBall_returnsConesYAxisConstraint() {
        double[] x = new double[]{0.5, 0.5};
        Optional<double[]> hyperplane = cone.getSeparatingHyperplane(x);
        assertTrue(hyperplane.isPresent());
        assertArrayEquals(new double[] {0, 1}, hyperplane.get(), 1e-10);
    }
}