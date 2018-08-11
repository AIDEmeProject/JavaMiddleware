package classifier.linear;

import data.DataPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MarginClassifierTest {
    private MarginClassifier classifier;
    
    @Test
    void predict_positiveMargin_returnPositiveLabel() {
        classifier = mock(MarginClassifier.class);
        when(classifier.predict((DataPoint) any())).thenCallRealMethod();
        when(classifier.margin((DataPoint) any())).thenReturn(1.);
        assertEquals(1, classifier.predict(mock(DataPoint.class)));
    }

    @Test
    void predict_negativeMargin_returnNegativeLabel() {
        classifier = mock(MarginClassifier.class);
        when(classifier.predict((DataPoint) any())).thenCallRealMethod();
        when(classifier.margin((DataPoint) any())).thenReturn(-1.);
        assertEquals(0, classifier.predict(mock(DataPoint.class)));
    }

    @Test
    void predict_zeroMargin_returnNegativeLabel() {
        classifier = mock(MarginClassifier.class);
        when(classifier.predict((DataPoint) any())).thenCallRealMethod();
        when(classifier.margin((DataPoint) any())).thenReturn(0.);
        assertEquals(0, classifier.predict(mock(DataPoint.class)));
    }

    @Test
    void probability_positiveMargin_returnExpectedProbability() {
        classifier = mock(MarginClassifier.class);
        when(classifier.probability((DataPoint) any())).thenCallRealMethod();
        when(classifier.margin((DataPoint) any())).thenReturn(1.);
        assertEquals(0.731058579, classifier.probability(mock(DataPoint.class)), 1e-8);
    }

    @Test
    void probability_negativeMargin_returnExpectedProbability() {
        classifier = mock(MarginClassifier.class);
        when(classifier.probability((DataPoint) any())).thenCallRealMethod();
        when(classifier.margin((DataPoint) any())).thenReturn(-1.);
        assertEquals(0.268941421, classifier.probability(mock(DataPoint.class)), 1e-8);
    }

    @Test
    void probability_zeroMargin_returnOneHalf() {
        classifier = mock(MarginClassifier.class);
        when(classifier.probability((DataPoint) any())).thenCallRealMethod();
        when(classifier.margin((DataPoint) any())).thenReturn(0.);
        assertEquals(0.5, classifier.probability(mock(DataPoint.class)), 1e-8);
    }
}