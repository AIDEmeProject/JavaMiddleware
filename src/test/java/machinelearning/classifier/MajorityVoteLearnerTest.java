package machinelearning.classifier;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.VersionSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class MajorityVoteLearnerTest {
    private MajorityVoteLearner learner;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        learner = new MajorityVoteLearner(mock(VersionSpace.class), 1);
    }

    @Test
    void constructor_NullInputVersionSpace_throwsException() {
        assertThrows(NullPointerException.class, () -> new MajorityVoteLearner<>(null, 1));
    }

    @Test
    @SuppressWarnings("unchecked")
    void constructor_NegativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteLearner(mock(VersionSpace.class), -1));
    }

    @Test
    @SuppressWarnings("unchecked")
    void constructor_ZeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteLearner(mock(VersionSpace.class), 0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void fit_mockedVersionSpace_versionSpaceIsSampledOnceWithCorrectArguments() {
        // version space mock
        VersionSpace versionSpace = mock(VersionSpace.class);
        when(versionSpace.sample(any(), anyInt())).thenReturn(mock(MajorityVote.class));

        // majority vote learner
        int sampleSize = 5;
        learner = new MajorityVoteLearner(versionSpace, sampleSize);

        // fit and verify
        LabeledDataset labeledDataset = mock(LabeledDataset.class);
        learner.fit(labeledDataset);
        verify(versionSpace, times(1)).sample(any(), eq(sampleSize));
    }
}