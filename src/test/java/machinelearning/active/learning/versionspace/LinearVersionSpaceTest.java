package machinelearning.active.learning.versionspace;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.classifier.MajorityVote;
import machinelearning.classifier.margin.LinearClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;
import utils.linprog.LinearProgramSolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class LinearVersionSpaceTest {
    private LabeledDataset trainingData;
    private HitAndRunSampler sampler;
    private LinearVersionSpace versionSpace;
    private Vector[] hitAndRunSamples;

    @BeforeEach
    void setUp() {
        trainingData = mock(LabeledDataset.class);

        hitAndRunSamples = new Vector[] {
                Vector.FACTORY.make(1,2),
                Vector.FACTORY.make(3,4),
                Vector.FACTORY.make(5,6)
        };

        sampler = mock(HitAndRunSampler.class);
        when(sampler.sample(any(), anyInt())).thenReturn(hitAndRunSamples);

        versionSpace = new LinearVersionSpace(sampler, mock(LinearProgramSolver.FACTORY.class));
    }

    @Test
    void sample_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> versionSpace.sample(trainingData, -1));
    }

    @Test
    void sample_zeroNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> versionSpace.sample(trainingData, 0));
    }

    @Test
    void sample_validInput_HitAndRunSamplerCalledOnce() {
        versionSpace.sample(trainingData, hitAndRunSamples.length);
        verify(sampler).sample(any(), eq(hitAndRunSamples.length));
    }

    @Test
    void sample_noIntercept_sampleOutputsTheExpectedLinearClassifiers() {
        LinearClassifier[] expected = new LinearClassifier[hitAndRunSamples.length];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = new LinearClassifier(hitAndRunSamples[i], false);
        }

        assertEquals(new MajorityVote<>(expected), versionSpace.sample(trainingData, hitAndRunSamples.length));
    }

    //TODO: write better tests

//    @Test
//    void sample_addIntercept_sampleOutputsTheExpectedLinearClassifiers() {
//        versionSpace.addIntercept();
//
//        LinearClassifier[] expected = new LinearClassifier[hitAndRunSamples.length];
//        for (int i = 0; i < expected.length; i++) {
//            expected[i] = new LinearClassifier(hitAndRunSamples[i], true);
//        }
//
//        assertArrayEquals(expected, versionSpace.sample(trainingData, hitAndRunSamples.length));
//    }
}