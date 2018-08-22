package machinelearning.active.learning;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import machinelearning.classifier.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

class GeneralizedBinarySearchTest {
    private Collection<LabeledPoint> trainingData;
    private MajorityVoteClassifier majorityVoteClassifier;
    private MajorityVoteLearner majorityVoteLearner;
    private GeneralizedBinarySearch generalizedBinarySearch;

    @BeforeEach
    void setUp() {
        majorityVoteClassifier = mock(MajorityVoteClassifier.class);

        majorityVoteLearner = mock(MajorityVoteLearner.class);
        when(majorityVoteLearner.fit(anyCollection())).thenReturn(majorityVoteClassifier);

        trainingData = new ArrayList<>();
        trainingData.add(mock(LabeledPoint.class));
    }

    @Test
    void fit_singleParameterConstructor_fitResultIsMajorityVoteClassifier() {
        generalizedBinarySearch = new GeneralizedBinarySearch(majorityVoteLearner);
        assertSame(majorityVoteClassifier, generalizedBinarySearch.fit(trainingData));
    }

    @Test
    void fit_singleParameterConstructor_majorityVoteLearnerCalledOnlyOnce() {
        generalizedBinarySearch = new GeneralizedBinarySearch(majorityVoteLearner);
        generalizedBinarySearch.fit(trainingData);
        verify(majorityVoteLearner).fit(trainingData);
    }

    @Test
    void fit_twoParameterConstructorWithIdenticalLearners_majorityVoteLearnerCalledOnlyOnce() {
        generalizedBinarySearch = new GeneralizedBinarySearch(majorityVoteLearner, majorityVoteLearner);
        generalizedBinarySearch.fit(trainingData);
        verify(majorityVoteLearner).fit(trainingData);
    }

    @Test
    void fit_twoParameterConstructorWithIdenticalLearners_majorityVoteLearnerOnlyCalledOnce() {
        generalizedBinarySearch = new GeneralizedBinarySearch(majorityVoteLearner, majorityVoteLearner);
        Classifier result = generalizedBinarySearch.fit(trainingData);
        assertSame(result, majorityVoteClassifier);
    }


    @Test
    void fit_twoLearnersConstructor_eachLearnerIsOnlyCalledOnce() {
        Classifier classifier = mock(Classifier.class);
        Learner learner = mock(Learner.class);
        when(learner.fit(anyCollection())).thenReturn(classifier);

        generalizedBinarySearch = new GeneralizedBinarySearch(learner, majorityVoteLearner);
        generalizedBinarySearch.fit(trainingData);

        verify(majorityVoteLearner).fit(trainingData);
        verify(learner).fit(trainingData);
    }

    @Test
    void fit_twoLearnersConstructor_activeLearnerOutputsLearnerTrainedClassifier() {
        Classifier classifier = mock(Classifier.class);
        Learner learner = mock(Learner.class);
        when(learner.fit(anyCollection())).thenReturn(classifier);

        generalizedBinarySearch = new GeneralizedBinarySearch(learner, majorityVoteLearner);
        Classifier result = generalizedBinarySearch.fit(trainingData);

        assertSame(classifier, result);
    }

    @Test
    void retrieveMostInformativeUnlabeledPoint_unlabeledSetOfThreePoints_majorityVoteClassifierProbabilityCalledThreeTimes() {
        Collection<DataPoint> unlabeledSet = Arrays.asList(
                mock(DataPoint.class),
                mock(DataPoint.class),
                mock(DataPoint.class)
        );

        LabeledDataset dataset = mock(LabeledDataset.class);
        when(dataset.getNumUnlabeledPoints()).thenReturn(3);
        when(dataset.getUnlabeledPoints()).thenReturn(unlabeledSet);

        when(majorityVoteClassifier.probability((DataPoint) any())).thenReturn(0.4, 0.5, 0.8);

        generalizedBinarySearch = new GeneralizedBinarySearch(majorityVoteLearner);
        generalizedBinarySearch.fit(trainingData);
        generalizedBinarySearch.retrieveMostInformativeUnlabeledPoint(dataset);

        verify(majorityVoteClassifier, times(3)).probability((DataPoint) any());
    }

    @Test
    void retrieveMostInformativeUnlabeledPoint_unlabeledSetOfThreePoints_mostUncertainPointCorrectlyRetrieved() {
        List<DataPoint> unlabeledSet = Arrays.asList(
                mock(DataPoint.class),
                mock(DataPoint.class),
                mock(DataPoint.class)
        );

        LabeledDataset dataset = mock(LabeledDataset.class);
        when(dataset.getNumUnlabeledPoints()).thenReturn(3);
        when(dataset.getUnlabeledPoints()).thenReturn(unlabeledSet);

        when(majorityVoteClassifier.probability((DataPoint) any())).thenReturn(0.4, 0.5, 0.8);

        generalizedBinarySearch = new GeneralizedBinarySearch(majorityVoteLearner);
        generalizedBinarySearch.fit(trainingData);
        DataPoint result = generalizedBinarySearch.retrieveMostInformativeUnlabeledPoint(dataset);

        assertSame(unlabeledSet.get(1), result);
    }
}