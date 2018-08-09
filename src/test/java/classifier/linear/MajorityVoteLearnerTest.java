package classifier.linear;

import org.junit.jupiter.api.Test;
import utils.versionspace.VersionSpace;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class MajorityVoteLearnerTest {
    @Test
    void constructor_NullInputVersionSpace_throwsException() {
        assertThrows(NullPointerException.class, () -> new MajorityVoteLearner(null, 1));
    }

    @Test
    void constructor_NegativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteLearner(mock(VersionSpace.class), -1));
    }

    @Test
    void constructor_ZeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteLearner(mock(VersionSpace.class), 0));
    }
}