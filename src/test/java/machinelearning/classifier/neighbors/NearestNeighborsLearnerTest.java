package machinelearning.classifier.neighbors;

import machinelearning.classifier.AbstractLearnerTest;
import data.DataPoint;
import data.LabeledPoint;
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

        knn = new NearestNeighborsLearner(1, 0);
        knn.initialize(points);
        learner = knn;
    }

    @Test
    void constructor_negativeK_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(-1, 0));
    }

    @Test
    void constructor_zeroK_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(0, 0));
    }

    @Test
    void constructor_negativeGamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(1, -1));
    }

    @Test
    void constructor_largerThan1Gamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsLearner(1, 2));
    }

    @Test
    void initialize_KEqualsTo1_constructsCorrectIndexes() {
        int[][] indexes = knn.getIndexes();

        assertArrayEquals(new int[] {2}, indexes[0]);
        assertArrayEquals(new int[] {0}, indexes[1]);
        assertArrayEquals(new int[] {0}, indexes[2]);
    }

    @Test
    void initialize_KEqualsTo2_constructsCorrectIndexes() {
        knn = new NearestNeighborsLearner(2, 0);
        knn.initialize(points);

        int[][] indexes = knn.getIndexes();

        assertArrayEquals(new int[] {1,2}, indexes[0]);
        assertArrayEquals(new int[] {0,2}, indexes[1]);
        assertArrayEquals(new int[] {0,1}, indexes[2]);
    }

    @Test
    void fit_emptyInputCollection_throwsException() {
        knn = new NearestNeighborsLearner(2, 0);
        assertThrows(IllegalArgumentException.class, () -> knn.fit(new ArrayList<>()));
    }

    @Test
    void fit_initializeNeverCalled_throwsException() {
        Collection<LabeledPoint> labeledPoints = new ArrayList<>();
        for (DataPoint point : points){
            labeledPoints.add(new LabeledPoint(point, 1));
        }

        knn = new NearestNeighborsLearner(2, 0);
        assertThrows(NullPointerException.class, () -> knn.fit(labeledPoints));
    }
}