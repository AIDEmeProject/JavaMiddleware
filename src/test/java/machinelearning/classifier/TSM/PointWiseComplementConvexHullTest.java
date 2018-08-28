package machinelearning.classifier.TSM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWiseComplementConvexHullTest {
    private double[][] posVertex;
    private double[] negVertex;
    private PointWiseComplementConvexHull pointWiseComplementConvexHull;

    @BeforeEach
    void setUp() {
        posVertex = new double[3][];
        posVertex[0] = new double[]{0,0,1};
        posVertex[1] = new double[]{0,1,0};
        posVertex[2] = new double[]{1,0,0};
        negVertex = new double[]{1,1,1};
        pointWiseComplementConvexHull = new PointWiseComplementConvexHull(3, negVertex, posVertex);

    }


    @Test
    void addVertex_fail_numberOfFacet() {
        pointWiseComplementConvexHull.addVertex(new double[]{0,0.5,0.5});
        assertEquals(3, pointWiseComplementConvexHull.getFacets().size());
    }


    @Test
    void addVertex_success_numberOfFacet() {
        pointWiseComplementConvexHull.addVertex(new double[]{0,1,1});
        assertEquals(4, pointWiseComplementConvexHull.getFacets().size());
    }

    @Test
    void containsPoint_internalPoint() {
        assertEquals(true, pointWiseComplementConvexHull.containsPoint(new double[]{2,2,2}));
    }

    @Test
    void containsPoint_onBoundary() {
        assertEquals(true, pointWiseComplementConvexHull.containsPoint(new double[]{2,2,1}));
    }

    @Test
    void containsPoint_externalPoint() {
        assertEquals(false, pointWiseComplementConvexHull.containsPoint(new double[]{0,0,0}));
    }

    @Test
    void getFacets() {
        assertEquals(3, pointWiseComplementConvexHull.getFacets().size());
    }
}