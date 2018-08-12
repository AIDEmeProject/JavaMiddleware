package machinelearning.classifier;

import data.DataPoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MajorityVoteClassifierTest {
    @Test
    void constructor_emptyClassifierArrayInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteClassifier(new Classifier[0]));
    }

    @Test
    void constructor_NullInClassifierInputArray_throwsException() {
        assertThrows(NullPointerException.class, () -> new MajorityVoteClassifier(new Classifier[]{null}));
    }

    @Test
    void probability_OnePositiveClassifierAndOneNegativeClassifier_ProbabilityCorrectlyComputed() {
        probability_PositiveAndNegativeClassifiers_ProbabilityCorrectlyComputed(1, 1);
    }

    @Test
    void probability_TwoPositiveClassifiersAndOneNegativeClassifier_ProbabilityCorrectlyComputed() {
       probability_PositiveAndNegativeClassifiers_ProbabilityCorrectlyComputed(2, 1);
    }

    @Test
    void probability_TwoNegativeClassifiersAndOnePositiveClassifier_ProbabilityCorrectlyComputed() {
        probability_PositiveAndNegativeClassifiers_ProbabilityCorrectlyComputed(1, 2);
    }

    private void probability_PositiveAndNegativeClassifiers_ProbabilityCorrectlyComputed(int pos, int neg){
        Classifier positiveClassifier = Mockito.mock(Classifier.class);
        Mockito.when(positiveClassifier.predict(Mockito.any(DataPoint.class))).thenReturn(Label.POSITIVE);

        Classifier negativeClassifier = Mockito.mock(Classifier.class);
        Mockito.when(negativeClassifier.predict(Mockito.any(DataPoint.class))).thenReturn(Label.NEGATIVE);

        Classifier[] classifiers = new Classifier[pos+neg];
        for (int i = 0; i < classifiers.length; i++) {
            classifiers[i] = i < pos ? positiveClassifier : negativeClassifier;
        }

        MajorityVoteClassifier majorityVote = new MajorityVoteClassifier(classifiers);
        DataPoint point = Mockito.mock(DataPoint.class);
        assertEquals((double) pos / classifiers.length, majorityVote.probability(point));
    }
}