package explore.metrics;

import data.IndexedDataset;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.user.FactoredUser;
import explore.user.User;
import machinelearning.classifier.*;
import machinelearning.threesetmetric.LabelGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubspatialConfusionMatrixCalculatorTest {
    private IndexedDataset dataset;
    private PartitionedDataset data;
    private User user;

    private SubspatialLearner learner;
    private SubspatialConfusionMatrixCalculator calculator;

    @BeforeEach
    void setUp() {
        dataset = new IndexedDataset(Arrays.asList(0L, 1L, 2L), Matrix.FACTORY.make(3, 2, 1, 2, 3, 4, 5, 6));
        dataset.setFactorizationStructure(new int[][] {{0}, {1}});

        data = new PartitionedDataset(dataset);
        data.update(new LabeledPoint(0, Vector.FACTORY.make(1, 2), new LabelGroup(Label.POSITIVE, Label.POSITIVE)));

        user = new FactoredUser(Arrays.asList(buildSet(0, 1), buildSet(1, 2)));

        learner = new SubspatialLearner(new Learner[] {mock(Learner.class), mock(Learner.class)});
        calculator = new SubspatialConfusionMatrixCalculator(learner);
    }

    private Set<Long> buildSet(long... values) {
        Set<Long> set = new HashSet<>();
        for (long value : values)
            set.add(value);
        return set;
    }


    @Test
    void compute_dataAndLearnerHaveIncompatibleNumberOfPartitions_throwsException() {
        Learner lr = mock(Learner.class);
        learner = new SubspatialLearner(new Learner[]{lr});
        calculator = new SubspatialConfusionMatrixCalculator(learner);
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(data, user));
    }

    @Test
    void compute_dataAndUserHaveIncompatibleNumberOfPartitions_throwsException() {
        user = new FactoredUser(Arrays.asList(buildSet(1L)));
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(data, user));
    }
}