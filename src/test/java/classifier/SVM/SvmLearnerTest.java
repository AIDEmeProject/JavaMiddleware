package classifier.SVM;

import classifier.AbstractLearnerTest;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class SvmLearnerTest extends AbstractLearnerTest {
    @BeforeEach
    void setUp() {
        SvmParameterAdapter params = new SvmParameterAdapter();
        learner = new SvmLearner(params);
    }
}