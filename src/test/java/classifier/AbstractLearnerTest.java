package classifier;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractLearnerTest {
    protected Learner learner;

    @Test
    void fit_EmptyLabeledPointsCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> learner.fit(new ArrayList<>()));
    }
}
