package machinelearning.threesetmetric.TSM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RidgeTest {
    private Ridge ridge;
    private Vertex[] vertices;

    @BeforeEach
    void setUp(){
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{1,1,1});
        vertices = new Vertex[]{vertex_1, vertex_2};
        ridge = new Ridge(3, vertices);
    }

    @Test
    void ridge_constructorException(){
        assertThrows(IllegalArgumentException.class, () -> new Ridge(2, vertices));
    }

    @Test
    void getVertices() {
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{1,1,1});
        Vertex[] v = new Vertex[]{vertex_1, vertex_2};
        assertEquals(Arrays.deepToString(v), Arrays.deepToString(ridge.getVertices()));
    }

    @Test
    void equals() {
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{1,1,1});
        Ridge r = new Ridge(3, new Vertex[]{vertex_1, vertex_2});
        assertEquals(true, ridge.equals(r));
    }

    @Test
    void unEquals() {
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{0,1,1});
        Ridge r = new Ridge(3, new Vertex[]{vertex_1, vertex_2});
        assertEquals(false, ridge.equals(r));
    }

}