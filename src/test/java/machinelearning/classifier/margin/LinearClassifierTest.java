package machinelearning.classifier.margin;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearClassifierTest {
    private LinearClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new LinearClassifier(1, Vector.FACTORY.make(-1,2));
    }

    @Test
    void margin_incompatibleDimension_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> classifier.margin(Vector.FACTORY.zeros(3)));
    }

    @Test
    void margin_pointOnBoundary_returnsZero() {
        Vector point = Vector.FACTORY.make(-1, -1);
        assertEquals(0, classifier.margin(point));
    }

    @Test
    void margin_pointOnPositiveSideOfMargin_returnsCorrectMargin() {
        Vector point = Vector.FACTORY.make(-1, 3);
        assertEquals(8, classifier.margin(point));
    }

    @Test
    void margin_pointOnNegativeSideOfMargin_returnsCorrectMargin() {
        Vector point = Vector.FACTORY.make(1, -3);
        assertEquals(-6, classifier.margin(point));
    }

    @Test
    void predict_incompatibleDimension_throwsException() {
        DataPoint point = new DataPoint(0, new double[3]);
        assertThrows(IllegalArgumentException.class, () -> classifier.predict(point));
    }

    @Test
    void probability_incompatibleDimension_throwsException() {
        DataPoint point = new DataPoint(0, new double[3]);
        assertThrows(IllegalArgumentException.class, () -> classifier.probability(point));
    }
}