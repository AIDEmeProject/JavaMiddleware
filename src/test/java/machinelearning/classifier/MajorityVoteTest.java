package machinelearning.classifier;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MajorityVoteTest {
    @Test
    void constructor_emptyClassifierArrayInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVote<>(new Classifier[0]));
    }

    @Test
    void constructor_NullInClassifierInputArray_throwsException() {
        assertThrows(NullPointerException.class, () -> new MajorityVote<>(new Classifier[]{null}));
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
        Mockito.when(positiveClassifier.predict(Mockito.any(Vector.class))).thenReturn(Label.POSITIVE);

        Classifier negativeClassifier = Mockito.mock(Classifier.class);
        Mockito.when(negativeClassifier.predict(Mockito.any(Vector.class))).thenReturn(Label.NEGATIVE);

        Classifier[] classifiers = new Classifier[pos+neg];
        for (int i = 0; i < classifiers.length; i++) {
            classifiers[i] = i < pos ? positiveClassifier : negativeClassifier;
        }

        MajorityVote majorityVote = new MajorityVote<>(classifiers);
        Vector point = Mockito.mock(Vector.class);
        assertEquals((double) pos / classifiers.length, majorityVote.probability(point));
    }
}