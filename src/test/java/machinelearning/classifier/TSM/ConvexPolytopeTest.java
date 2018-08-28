package machinelearning.classifier.TSM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvexPolytopeTest {
    private ConvexPolytope convexPolytope;
    private double[][] simplex;

    @BeforeEach
    void setUp(){
        simplex = new double[4][];
        simplex[0] = new double[]{0,0,0};
        simplex[1] = new double[]{0,0,1};
        simplex[2] = new double[]{0,1,0};
        simplex[3] = new double[]{1,0,0};
        convexPolytope = new ConvexPolytope(3, simplex);
    }

    @Test
    void addVertex_fail() {
        convexPolytope.addVertex(new double[]{0.1,0.1,0.1});
        assertEquals(4,convexPolytope.getFacets().size());
    }

    @Test
    void addVertex_number_of_facets() {
        convexPolytope.addVertex(new double[]{1,1,1});
        assertEquals(6,convexPolytope.getFacets().size());
    }

    @Test
    void addVertex_containVertex() {
        convexPolytope.addVertex(new double[]{1,1,1});
        assertEquals(true, convexPolytope.containsPoint(new double[]{1,1,1}));
    }

    @Test
    void getFacets() {
        assertEquals(4, convexPolytope.getFacets().size());
    }

    @Test
    void containsPoint_internalPoint() {
        assertEquals(true, convexPolytope.containsPoint(new double[]{0.25,0.25,0.25}));
    }

    @Test
    void containsPoint_onBoundary() {
        assertEquals(true, convexPolytope.containsPoint(new double[]{0,0.5,0.5}));

    }

    @Test
    void containsPoint_externalPoint() {
        assertEquals(false, convexPolytope.containsPoint(new double[]{1,1,1}));

    }

    @Test
    void randomSample() {
        assertEquals(true, convexPolytope.containsPoint(convexPolytope.randomSample()));
    }
}