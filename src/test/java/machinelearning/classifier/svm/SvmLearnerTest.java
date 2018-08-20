package machinelearning.classifier.svm;

import machinelearning.classifier.AbstractLearnerTest;
import org.junit.jupiter.api.BeforeEach;

class SvmLearnerTest extends AbstractLearnerTest {
    @BeforeEach
    void setUp() {
        SvmParameterAdapter params = new SvmParameterAdapter();
        learner = new SvmLearner(params);
    }
}