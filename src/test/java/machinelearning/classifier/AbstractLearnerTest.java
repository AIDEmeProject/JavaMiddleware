package machinelearning.classifier;

import data.LabeledDataset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractLearnerTest {
    protected Learner learner;

    @Test
    void fit_EmptyLabeledPointsCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> learner.fit(LabeledDataset.EMPTY));
    }
}
