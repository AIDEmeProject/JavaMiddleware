package machinelearning.active.learning.versionspace;

import data.LabeledPoint;
import machinelearning.active.learning.versionspace.convexbody.DummySampleCache;
import machinelearning.active.learning.versionspace.convexbody.SampleCache;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunChain;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.active.learning.versionspace.convexbody.sampling.SampleSelector;
import machinelearning.classifier.margin.LinearClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import utils.linprog.LinearProgramSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class LinearVersionSpaceTest {
    private List<LabeledPoint> trainingData;
    private HitAndRunSampler sampler;
    private SampleSelector sampleSelector;
    private LinearVersionSpace versionSpace;
    private double[][] hitAndRunSamples;

    @BeforeEach
    void setUp() {
        trainingData = Arrays.asList(mock(LabeledPoint.class));

        hitAndRunSamples = new double[][] {{1,2}, {3,4}, {5,6}};

        sampler = mock(HitAndRunSampler.class);

        sampleSelector = mock(SampleSelector.class);
        when(sampleSelector.select(any(), anyInt())).thenReturn(hitAndRunSamples);

        versionSpace = new LinearVersionSpace(sampler, sampleSelector, mock(LinearProgramSolver.FACTORY.class));
    }

    @Test
    void sample_emptyLabeledPointCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> versionSpace.sample(new ArrayList<>(), 10));
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
        versionSpace.sample(trainingData, 10);
        verify(sampler).newChain(any());
    }

    @Test
    void sample_noIntercept_sampleOutputsTheExpectedLinearClassifiers() {
        LinearClassifier[] expected = new LinearClassifier[hitAndRunSamples.length];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = new LinearClassifier(hitAndRunSamples[i], false);
        }

        assertArrayEquals(expected, versionSpace.sample(trainingData, hitAndRunSamples.length));
    }

    @Test
    void sample_addIntercept_sampleOutputsTheExpectedLinearClassifiers() {
        versionSpace.addIntercept();

        LinearClassifier[] expected = new LinearClassifier[hitAndRunSamples.length];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = new LinearClassifier(hitAndRunSamples[i], true);
        }

        assertArrayEquals(expected, versionSpace.sample(trainingData, hitAndRunSamples.length));
    }

    @Test
    void sample_mockSampleCache_cacheMethodsAreCalledOnlyOnce() {
        SampleCache cache = spy(DummySampleCache.class);

        versionSpace.setSampleCachingStrategy(cache);
        versionSpace.sample(trainingData, hitAndRunSamples.length);

        verify(cache).attemptToSetDefaultInteriorPoint(any());
        verify(cache).updateCache(hitAndRunSamples);
    }

    @Test
    void sample_mockSampleSelector_selectCalledOnceWithExpectedNumberOfSamples() {
        int numSamples = 10;
        versionSpace.sample(trainingData, numSamples);
        verify(sampleSelector).select(any(), eq(numSamples));
    }
}