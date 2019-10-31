package machinelearning.classifier;

import data.IndexedDataset;
import data.LabeledDataset;
import machinelearning.threesetmetric.LabelGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubspatialLearnerTest {
    private Learner[] learners;
    private SubspatialWorker worker;
    private SubspatialLearner learner;

    @BeforeEach
    void setUp() {
        learners = new Learner[]{mock(Learner.class), mock(Learner.class)};
        worker = mock(SubspatialWorker.class);
        learner = new SubspatialLearner(learners, worker);
    }

    @Test
    void constructor_emptyLearnersArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SubspatialLearner(new Learner[0], worker));
    }

    @Test
    void constructor_nullWorker_throwsException() {
        assertThrows(NullPointerException.class, () -> new SubspatialLearner(learners, null));
    }

    @Test
    void fit_incompatiblePartitionSizeAndNumberOfLearners_throwsException() {
        LabeledDataset labeledDataset = getLabeledDataset(false);

        assertThrows(IllegalArgumentException.class, () -> learner.fit(labeledDataset));
    }

    @Test
    void fit_twoSubspaces_learnerFittedOnEachSubspace() {
        LabeledDataset labeledDataset = getLabeledDataset(true);
        learner = new SubspatialLearner(learners, new SubspatialWorker(1));

        learner.fit(labeledDataset);

        verify(learners[0]).fit(labeledDataset.getPartitionedData()[0]);
        verify(learners[1]).fit(labeledDataset.getPartitionedData()[1]);
    }

    @Test
    void fit_twoSubspaces_subpatialClassifierCorrectlyConstructed() {
        LabeledDataset labeledDataset = getLabeledDataset(true);

        List<Classifier> ls = Arrays.asList(mock(Classifier.class), mock(Classifier.class));
        when(learners[0].fit(any())).thenReturn(ls.get(0));
        when(learners[1].fit(any())).thenReturn(ls.get(1));

        when(worker.run((List<Callable<Classifier>>) any())).thenReturn(ls);

        assertEquals(
                new SubspatialClassifier(new int[][]{{0}, {1}}, ls.toArray(new Classifier[0]), worker),
                learner.fit(labeledDataset)
        );
    }

    private LabeledDataset getLabeledDataset(boolean partition) {
        IndexedDataset data = new IndexedDataset(Arrays.asList(0L, 10L, 20L), Matrix.FACTORY.make(3, 2, 1, 2, 3, 4, 5, 6));
        data.setFactorizationStructure(partition ? new int[][] {{0}, {1}} : new int[][] {{0, 1}});

        return new LabeledDataset(
                data,
                new LabelGroup[]{
                        new LabelGroup(Label.POSITIVE, Label.POSITIVE),
                        new LabelGroup(Label.POSITIVE, Label.NEGATIVE),
                        new LabelGroup(Label.NEGATIVE, Label.NEGATIVE),
                }
        );
    }
}