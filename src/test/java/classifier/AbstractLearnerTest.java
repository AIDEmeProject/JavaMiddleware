package classifier;

import exceptions.EmptyLabeledSetException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractLearnerTest {
    protected Learner learner;

    @Test
    void fit_EmptyLabeledPointsCollection_throwsException() {
        System.out.println(learner);
        assertThrows(EmptyLabeledSetException.class, () -> learner.fit(new ArrayList<>()));
    }
}
