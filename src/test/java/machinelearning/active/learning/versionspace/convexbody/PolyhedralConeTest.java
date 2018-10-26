package machinelearning.active.learning.versionspace.convexbody;

import data.IndexedDataset;
import data.LabeledDataset;
import machinelearning.classifier.Label;
import machinelearning.classifier.margin.LinearClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.InequalitySign;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class PolyhedralConeTest {
    private LabeledDataset points;
    private PolyhedralCone cone;

    @BeforeEach
    void setUp() {
        Matrix data = Matrix.FACTORY.identity(2);
        points = new LabeledDataset(new IndexedDataset(Arrays.asList(0L, 1L), data), new Label[]{Label.POSITIVE, Label.NEGATIVE});
        cone = new PolyhedralCone(points, mock(LinearProgramSolver.FACTORY.class));
    }

    @Test
    void getDim_twoDimensionalLabeledPoints_returnsTwo() {
        assertEquals(2, cone.getDim());
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
    void isInside_pointOnBoundaryOfCone_returnsTrue() {
        assertTrue(cone.isInside(Vector.FACTORY.make(1, 0)));
        assertTrue(cone.isInside(Vector.FACTORY.make(0, -1)));
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
        Vector result = cone.getInteriorPoint();

        assertEquals(Vector.FACTORY.make(1, -1).normalize(0.9), result);
    }

    @Test
    void computeLineIntersection_lineOfWrongDimension_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(3), Vector.FACTORY.make(1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> cone.computeLineIntersection(line));
    }

    @Test
    void computeLineIntersection_lineDoesNotInterceptPolytope_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1, 1));
        assertThrows(RuntimeException.class, () -> cone.computeLineIntersection(line));
    }

    @Test
    void computeLineIntersection_lineContainsBoundaryOfPolytope_throwsException() {
        Line line = new Line(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1, 0));
        assertThrows(RuntimeException.class, () -> cone.computeLineIntersection(line));
    }

    @Test
    void computeLineIntersection_lineIntersectsPolytope_lineSegmentCorrectlyComputed() {
        Line line = new Line(Vector.FACTORY.make(0.5, -0.5), Vector.FACTORY.make(1, 0));
        LineSegment segment = cone.computeLineIntersection(line);
        assertEquals(-0.5, segment.getLeftBound());
        assertEquals(Math.sqrt(0.75) - 0.5, segment.getRightBound());
    }

    @Test
    void getSeparatingHyperplane_InputNormLargerThanOne_returnsHyperplanePerpendicularToInput() {
        Vector x = Vector.FACTORY.make(1, 1);
        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
        assertTrue(hyperplane.isPresent());
        assertEquals(new LinearClassifier(-2, x), hyperplane.get());
    }

    @Test
    void getSeparatingHyperplane_InputOnInteriorOfCone_returnsEmptyOption() {
        Vector x = Vector.FACTORY.make(0.5, -0.5);
        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
        assertFalse(hyperplane.isPresent());
    }

    @Test
    void getSeparatingHyperplane_InputOnBoundaryOfCone_returnsEmptyOption() {
        Vector x = Vector.FACTORY.make(0.5, 0);
        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
        assertFalse(hyperplane.isPresent());
    }

    @Test
    void getSeparatingHyperplane_InputWithNegativeFirstCoordinateButInsideUnitBall_returnsConesXAxisConstraint() {
        Vector x = Vector.FACTORY.make(-0.5, -0.5);
        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
        assertTrue(hyperplane.isPresent());
        assertEquals(new LinearClassifier(0, Vector.FACTORY.make(-1, 0)), hyperplane.get());
    }

    @Test
    void getSeparatingHyperplane_InputWithPositiveFirstAndSecondCoordinatesButInsideUnitBall_returnsConesYAxisConstraint() {
        Vector x = Vector.FACTORY.make(0.5, 0.5);
        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
        assertTrue(hyperplane.isPresent());
        assertEquals(new LinearClassifier(0, Vector.FACTORY.make(0, 1)), hyperplane.get());
    }
}