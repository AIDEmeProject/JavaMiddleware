package classifier.linear;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearClassifierTest {
    private LinearClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new LinearClassifier(1, new double[] {-1,2});
    }

    @Test
    void biasAndWeightConstructor_EmptyWeightArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LinearClassifier(0, new double[0]));
    }

    @Test
    void probability_InputOfWrongDimension_throwsException() {
        DataPoint point = new DataPoint(0, 0, new double[] {1,2,3});
        assertThrows(IllegalArgumentException.class, () -> classifier.predict(point));
    }

    @Test
    void probability_InputOnTheMargin_returnsOneHalf() {
        DataPoint point = new DataPoint(0, 0, new double[] {1,0});
        assertEquals(0.5, classifier.probability(point));
    }

    @Test
    void probability_InputOfLargeModule_correctProbabilityReturned() {
        DataPoint point = new DataPoint(0, 0, new double[] {1e8,0});
        assertEquals(0, classifier.probability(point));

        point = new DataPoint(0, 0, new double[] {-1e8,0});
        assertEquals(1, classifier.probability(point));
    }

    @Test
    void predict_InputOnTheMargin_returnsZero() {
        DataPoint point = new DataPoint(0, 0, new double[] {1,0});
        assertEquals(0, classifier.predict(point));
    }

    @Test
    void predict_InputOfLargeModule_correctLabelReturned() {
        DataPoint point = new DataPoint(0, 0, new double[] {1e8,0});
        assertEquals(0, classifier.predict(point));

        point = new DataPoint(0, 0, new double[] {-1e8,0});
        assertEquals(1, classifier.predict(point));
    }
}