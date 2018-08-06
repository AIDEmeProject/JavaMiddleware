package classifier;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MajorityVoteClassifierTest {
    private MajorityVoteClassifier majorityVote;

    @BeforeEach
    void setUp() {
        majorityVote = new MajorityVoteClassifier();
    }

    @Test
    void add_nullClassifier_throwsException() {
        assertThrows(NullPointerException.class, () -> majorityVote.add(null));
    }

    @Test
    void addAll_CollectionHasANullClassifier_throwsException() {
        Collection<Classifier> classifiers = new ArrayList<>();
        classifiers.add(null);
        assertThrows(NullPointerException.class, () -> majorityVote.addAll(classifiers));
    }

    @Test
    void probability_positiveClassifierAndNegativeClassifier_returnsCorrectProbability() {
        Classifier positiveClassifier = Mockito.mock(Classifier.class);
        Mockito.when(positiveClassifier.predict(Mockito.any(DataPoint.class))).thenReturn(1);

        Classifier negativeClassifier = Mockito.mock(Classifier.class);
        Mockito.when(negativeClassifier.predict(Mockito.any(DataPoint.class))).thenReturn(0);

        majorityVote.add(positiveClassifier);
        majorityVote.add(negativeClassifier);

        assertEquals(0.5, majorityVote.probability(Mockito.mock(DataPoint.class)));
    }
}