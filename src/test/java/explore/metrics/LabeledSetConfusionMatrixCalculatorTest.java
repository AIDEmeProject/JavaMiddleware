package explore.metrics;

import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LabeledSetConfusionMatrixCalculatorTest {
    private PartitionedDataset data;
    private LabeledSetConfusionMatrixCalculator calculator;
    private User user;

    private Classifier classifierStub;
    private Learner learnerStub;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(-2L, new double[]{-2});
        builder.add(-1L, new double[]{-1});
        builder.add(0L, new double[]{0});
        builder.add(1L, new double[]{1});
        builder.add(2L, new double[]{2});

        data = new PartitionedDataset(builder.build());
        user = mock(User.class);

        // returns mock learner and classifier
        classifierStub = spy(Classifier.class);
        learnerStub = mock(Learner.class);
        when(learnerStub.fit(any())).thenReturn(classifierStub);

        // calculator
        calculator = new LabeledSetConfusionMatrixCalculator(learnerStub);
    }

    @Test
    void compute_emptyLabeledSet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(data, user));
    }

    @Test
    void compute_nonEmptyLabeledSet_userNeverCalled() {
        data.update(new LabeledPoint(-2L, new double[] {-2}, Label.NEGATIVE));
        calculator.compute(data, user);
        verify(user, never()).getLabel((DataPoint) any());
    }

    @Test
    void compute_mockedLearner_calledOnceOverTheLabeledSet() {
        data.update(new LabeledPoint(-2L, new double[] {-2}, Label.NEGATIVE));
        calculator.compute(data, user);
        verify(learnerStub).fit(data.getLabeledPoints());
    }

    @Test
    void compute_mockedClassifier_predictCalledOverLabeledSet() {
        data.update(new LabeledPoint(-2L, new double[] {-2}, Label.NEGATIVE));
        calculator.compute(data, user);
        verify(classifierStub).predict(data.getLabeledPoints().getData());
    }
}