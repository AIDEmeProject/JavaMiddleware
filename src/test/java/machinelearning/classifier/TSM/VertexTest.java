package machinelearning.classifier.TSM;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VertexTest {
    private double[] point;
    private Vertex vertex;

    @BeforeEach
    void setUp() {
        point = new double[] {3, -3};
        vertex  = new Vertex(2, point);}

    @Test
    void getValues() {
        assertArrayEquals(point, vertex.getValues());
    }

    @Test
    void compareTo_larger() {
        Vertex vertex_l = new Vertex(2, new double[] {1, -5});
        assertEquals(1, vertex.compareTo(vertex_l));
    }

    @Test
    void compareTo_smaller() {
        Vertex vertex_s = new Vertex(2, new double[] {4, 0});
        assertEquals(-1, vertex.compareTo(vertex_s));
    }

    @Test
    void compareTo_equal() {
        Vertex vertex_n = new Vertex(2, new double[] {3, -3});
        assertEquals(0, vertex.compareTo(vertex_n));
    }
}