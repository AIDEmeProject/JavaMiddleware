package machinelearning.classifier.TSM;

import java.util.Arrays;


/**
 * This class creates a ridge (or (dim-2)-face) based on a list of vertices
 *
 * @author lppeng, enhui
 */


public class Ridge {
    /**
     * The (dim-1) vertices that define the ridge
     */
    private final Vertex[] vertices;

    /**
     * Create a ridge based on the vertices, and check the number of vertices against the dimensionality of the space
     *
     * @param dim      the dimensionality of the space
     * @param vertices the vertices that define the ridge
     * @throws IllegalArgumentException wrong number of vertices
     */
    public Ridge(int dim, Vertex[] vertices) {
        if (vertices.length != dim - 1) {
            throw new IllegalArgumentException("Expected number of vertices = " + (dim - 1) + ", but get " + vertices.length + " vertices");
        }
        this.vertices = vertices;
        Arrays.sort(this.vertices);
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    /**
     * Specify the equal relationship for ridge
     *
     * @param a a ridge
     * @return true if two ridges are equal, false otherwise
     */
    public boolean equals(Object a) {
        if (this == a) {
            return true;
        }
        if (a == null || this.getClass() != a.getClass()) {
            return false;
        }
        Ridge _a = (Ridge) a;
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].compareTo(_a.vertices[i]) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the same hash code for "equal" ridges by using "deepHashCode" for an array of arrays
     *
     * @see java.util.Arrays
     *
     * @return a hash code for each ridge
     */
    public int hashCode() {
//        int result = 0;
//        for (Vertex vertice : vertices) {
//            result = result * 31 + vertice.hashCode();
//        }
//        return result;

        return Arrays.deepHashCode(vertices);
    }



    public String toString() {
        return Arrays.toString(vertices);
    }
}
