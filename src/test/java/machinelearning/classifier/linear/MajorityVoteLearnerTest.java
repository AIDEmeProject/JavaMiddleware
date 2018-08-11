package machinelearning.classifier.linear;

import machinelearning.classifier.Classifier;
import data.LabeledPoint;
import org.junit.jupiter.api.Test;
import machinelearning.active.learning.versionspace.VersionSpace;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void fit_mockedVersionSpace_versionSpaceIsSampledOnceWithCorrectArguments() {
        // version space mock
        VersionSpace versionSpace = mock(VersionSpace.class);
        when(versionSpace.sample(any(), anyInt())).thenReturn(new Classifier[] { mock(Classifier.class) });

        // majority vote learner
        int sampleSize = 1;
        MajorityVoteLearner learner = new MajorityVoteLearner(versionSpace, sampleSize);

        // fit and verify
        Collection<LabeledPoint> points = new ArrayList<>();
        learner.fit(points);
        verify(versionSpace, times(1)).sample(points, sampleSize);
    }
}