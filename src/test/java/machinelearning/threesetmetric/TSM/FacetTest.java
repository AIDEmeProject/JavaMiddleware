package machinelearning.threesetmetric.TSM;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FacetTest {
    private Facet facet;
    private Vertex[] vertices;
    private Vertex ref;

    @BeforeEach
    void setUp() {
        Vertex v1 = new Vertex(3, new double[]{0,0,0});
        Vertex v2 = new Vertex(3, new double[]{0,0,1});
        Vertex v3 = new Vertex(3, new double[]{0,1,0});
        vertices = new Vertex[3];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        ref = new Vertex(3, new double[]{1,1,1});
        facet = new Facet(3, vertices, ref, true);
    }

    @Test
    void getCoef(){
        assertArrayEquals(new double[]{-1,-0.0,0}, facet.getCoef());
    }

    @Test
    void getOffset(){
        assertEquals(0, facet.getOffset());
    }

    @Test
    void getVertices() {
        assertEquals(vertices, facet.getVertices());
    }

    @Test
    void getRidge() {
        int dim = vertices.length;
        Ridge[] ridges = new Ridge[dim];
        ridges[0] = new Ridge(dim, Arrays.copyOfRange(vertices, 1,3));
        Vertex[] v = new Vertex[2];
        v[0] = new Vertex(3, new double[]{0,0,0});
        v[1] = new Vertex(3, new double[]{0,1,0});
        ridges[1] = new Ridge(dim, v);
        ridges[2] = new Ridge(dim, Arrays.copyOfRange(vertices, 0,2));
        assertArrayEquals(ridges, facet.getRidge());
    }

    @Test
    void getRidge1() {
        Pair<Ridge, Vertex>[] pairs = new Pair[2];
        Vertex[] v = new Vertex[2];
        v[0] = new Vertex(3, new double[]{0,0,0});
        v[1] = new Vertex(3, new double[]{0,1,0});
        pairs[0] = new Pair<>(new Ridge(3, v), vertices[1]);
        pairs[1] = new Pair<>(new Ridge(3, Arrays.copyOfRange(vertices, 0,2)), vertices[2]);
        assertArrayEquals(pairs, facet.getRidge(vertices[0]));
    }

    @Test
    void notVisible() {
        double[] point = new double[]{1,2,3};
        assertEquals(-1, facet.isVisible(point));
    }

    @Test
    void isVisible(){
        double[] point = new double[]{-1,2,3};
        assertEquals(1, facet.isVisible(point));
    }

    @Test
    void onFacet(){
        double[] point = new double[]{0,2,3};
        assertEquals(0, facet.isVisible(point));
    }
}