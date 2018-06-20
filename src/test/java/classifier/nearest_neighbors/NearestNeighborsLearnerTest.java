package classifier.nearest_neighbors;

import classifier.AbstractLearnerTest;
import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class NearestNeighborsLearnerTest extends AbstractLearnerTest {

    private Collection<DataPoint> points;
    private NearestNeighborsLearner knn;

    @BeforeEach
    void setUp() {
        points = new ArrayList<>();
        points.add(new DataPoint(0, new double[] {3}));
        points.add(new DataPoint(1, new double[] {1}));
        points.add(new DataPoint(2, new double[] {4}));

        knn = new NearestNeighborsLearner(points, 1, 0);
        learner = knn;
    }

    @Test
    void constructor_negativeK_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(points, -1, 0));
    }

    @Test
    void constructor_zeroK_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(points, 0, 0));
    }

    @Test
    void constructor_negativeGamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(points, 1, -1));
    }

    @Test
    void constructor_largerThan1Gamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(points, 1, 2));
    }

    @Test
    void constructor_KEqualsTo1_constructsCorrectIndexes() {
        int[][] indexes = knn.getIndexes();

        assertArrayEquals(new int[] {2}, indexes[0]);
        assertArrayEquals(new int[] {0}, indexes[1]);
        assertArrayEquals(new int[] {0}, indexes[2]);
    }

    @Test
    void constructor_KEqualsTo2_constructsCorrectIndexes() {
        knn = new NearestNeighborsLearner(points, 2, 0);
        int[][] indexes = knn.getIndexes();

        assertArrayEquals(new int[] {1,2}, indexes[0]);
        assertArrayEquals(new int[] {0,2}, indexes[1]);
        assertArrayEquals(new int[] {0,1}, indexes[2]);
    }
}