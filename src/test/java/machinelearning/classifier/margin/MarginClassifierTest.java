package machinelearning.classifier.margin;

import machinelearning.classifier.Label;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MarginClassifierTest {
    private MarginClassifier classifier;
    
    @Test
    void predict_positiveMargin_returnPositiveLabel() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(1.);
        assertEquals(Label.POSITIVE, classifier.predict(mock(Vector.class)));
    }

    @Test
    void predict_negativeMargin_returnNegativeLabel() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(-1.);
        assertEquals(Label.NEGATIVE, classifier.predict(mock(Vector.class)));
    }

    @Test
    void predict_zeroMargin_returnNegativeLabel() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(0.);
        assertEquals(Label.NEGATIVE, classifier.predict(mock(Vector.class)));
    }

    @Test
    void probability_positiveMargin_returnExpectedProbability() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(1.);
        assertEquals(0.731058579, classifier.probability(mock(Vector.class)), 1e-8);
    }

    @Test
    void probability_negativeMargin_returnExpectedProbability() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(-1.);
        assertEquals(0.268941421, classifier.probability(mock(Vector.class)), 1e-8);
    }

    @Test
    void probability_zeroMargin_returnOneHalf() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(0.);
        assertEquals(0.5, classifier.probability(mock(Vector.class)), 1e-8);
    }
}