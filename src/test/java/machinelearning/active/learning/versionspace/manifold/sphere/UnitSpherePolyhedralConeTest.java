package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.euclidean.Line;
import machinelearning.active.learning.versionspace.manifold.euclidean.PolyhedralCone;
import machinelearning.active.learning.versionspace.manifold.euclidean.UnitBallPolyhedralCone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.LinearProgramSolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitSpherePolyhedralConeTest {
    private Matrix data;
    private PolyhedralCone cone;
    private UnitSpherePolyhedralCone body;

    @BeforeEach
    void setUp() {
        data = Matrix.FACTORY.make(3, 3, 1, 0, 0, 0, -1, 0, 0, 0, 1);
        cone = new PolyhedralCone(data, mock(LinearProgramSolver.FACTORY.class));
        body = new UnitSpherePolyhedralCone(cone);
    }

    @Test
    void dim_threeDimensionalSpace_returnsTwo() {
        assertEquals(3, body.dim());
    }

    @Test
    void isInside_invalidDimensionInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> body.isInside(Vector.FACTORY.zeros(1)));
    }

    @Test
    void isInside_pointOnInterior_returnsTrue() {
        double val = 1 / Math.sqrt(3);
        assertTrue(body.isInside(Vector.FACTORY.make(val, -val, val)));
    }

    @Test
    void isInside_pointOnExterior_returnsFalse() {
        double val = 1 / Math.sqrt(3);
        assertFalse(body.isInside(Vector.FACTORY.make(0, 0, 0)));
        assertFalse(body.isInside(Vector.FACTORY.make(val, val, val)));
        assertFalse(body.isInside(Vector.FACTORY.make(2*val, -2*val, 2*val)));
    }

    @Test
    void isInside_pointOnBoundary_returnsTrue() {
        assertTrue(body.isInside(Vector.FACTORY.make(0, 0, 1)));
        assertTrue(body.isInside(Vector.FACTORY.make(0, -1, 0)));
        assertTrue(body.isInside(Vector.FACTORY.make(1, 0, 0)));
    }

    @Test
    void getInteriorPoint_mockPolyhedralCone_mockGetInteriorCalledOnce() {
        double val = 1 / Math.sqrt(3);
        cone = mock(PolyhedralCone.class);
        when(cone.getInteriorPoint()).thenReturn(Vector.FACTORY.make(val, -val, val));

        body = new UnitSpherePolyhedralCone(cone);
        body.getInteriorPoint();

        verify(cone).getInteriorPoint();
    }

    @Test
    void computeIntersection_greatCircleOfWrongDimension_throwsException() {
        GreatCircle line = new GreatCircle(Vector.FACTORY.make(1, 0), Vector.FACTORY.make(0, 1));
        assertThrows(IllegalArgumentException.class, () -> body.computeIntersection(line));
    }

    @Test
    void computeIntersection_centerOutsideBody_throwsException() {
        GreatCircle line = new GreatCircle(Vector.FACTORY.make(1, 1, 1), Vector.FACTORY.make(1, -1, 0));
        assertThrows(RuntimeException.class, () -> body.computeIntersection(line));
    }

//    @Test
//    void computeIntersection_greatCircleDoesNotInterceptBody_throwsException() {
//        initialize(Matrix.FACTORY.identity(3).iScalarMultiply(-1));  // x <= 0, y <= 0, z <= 0
//
//        GreatCircle line = new GreatCircle(Vector.FACTORY.make(0, 0, 1), Vector.FACTORY.make(1, -1, 0));
//        assertThrows(RuntimeException.class, () -> body.computeIntersection(line));
//    }

    @Test
    void computeIntersection_greatCircleContainsBoundaryOfPolytope_throwsException() {
        GreatCircle line = new GreatCircle(Vector.FACTORY.make(0, 0, 1), Vector.FACTORY.make(1, 0, 0));
        GeodesicSegment segment = body.computeIntersection(line);
        assertEquals(0, segment.getLowerBound(), 1e-10);
        assertEquals(Math.PI/2, segment.getUpperBound(), 1e-10);
    }

    @Test
    void computeIntersection_lineIntersectsPolytope_lineSegmentCorrectlyComputed() {
        GreatCircle line = new GreatCircle(Vector.FACTORY.make(1, -1, 1), Vector.FACTORY.make(1, 1, 0));
        GeodesicSegment segment = body.computeIntersection(line);
        assertEquals(-Math.atan2(Math.sqrt(2), Math.sqrt(3)), segment.getLowerBound(), 1e-10);
        assertEquals(Math.atan2(Math.sqrt(2), Math.sqrt(3)), segment.getUpperBound(), 1e-10);
    }

//    @Test
//    void getSeparatingHyperplane_InputNormLargerThanOne_returnsHyperplanePerpendicularToInput() {
//        Vector x = Vector.FACTORY.make(1, 1);
//        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
//        assertTrue(hyperplane.isPresent());
//        assertEquals(new LinearClassifier(-1, x.normalize(1.0)), hyperplane.get());
//    }
//
//    @Test
//    void getSeparatingHyperplane_InputOnInteriorOfCone_returnsEmptyOption() {
//        Vector x = Vector.FACTORY.make(0.5, -0.5);
//        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
//        assertFalse(hyperplane.isPresent());
//    }
//
//    @Test
//    void getSeparatingHyperplane_InputOnBoundaryOfCone_returnsEmptyOption() {
//        Vector x = Vector.FACTORY.make(0.5, 0);
//        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
//        assertFalse(hyperplane.isPresent());
//    }
//
//    @Test
//    void getSeparatingHyperplane_InputWithNegativeFirstCoordinateButInsideUnitBall_returnsConesXAxisConstraint() {
//        Vector x = Vector.FACTORY.make(-0.5, -0.5);
//        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
//        assertTrue(hyperplane.isPresent());
//        assertEquals(new LinearClassifier(0, Vector.FACTORY.make(-1, 0)), hyperplane.get());
//    }
//
//    @Test
//    void getSeparatingHyperplane_InputWithPositiveFirstAndSecondCoordinatesButInsideUnitBall_returnsConesYAxisConstraint() {
//        Vector x = Vector.FACTORY.make(0.5, 0.5);
//        Optional<LinearClassifier> hyperplane = cone.getSeparatingHyperplane(x);
//        assertTrue(hyperplane.isPresent());
//        assertEquals(new LinearClassifier(0, Vector.FACTORY.make(0, 1)), hyperplane.get());
//    }
}