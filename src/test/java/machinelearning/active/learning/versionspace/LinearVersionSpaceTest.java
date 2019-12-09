package machinelearning.active.learning.versionspace;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.euclidean.UnitBallPolyhedralCone;
import machinelearning.classifier.Label;
import machinelearning.classifier.LinearMajorityVote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class LinearVersionSpaceTest {
    private LabeledDataset trainingData;
    private HitAndRunSampler sampler;
    private LinearVersionSpace versionSpace;
    private Vector[] hitAndRunSamples;
    private LinearProgramSolver.FACTORY solver;

    @BeforeEach
    void setUp() {
        Matrix X = Matrix.FACTORY.make(2, 1, 10, 20);
        Label[] y = new Label[]{Label.POSITIVE, Label.NEGATIVE};
        trainingData = new LabeledDataset(Arrays.asList(1L, 2L), X, y);

        sampler = mock(HitAndRunSampler.class);

        hitAndRunSamples = new Vector[] {
                Vector.FACTORY.make(1,2),
                Vector.FACTORY.make(3,4),
                Vector.FACTORY.make(5,6)
        };


        when(sampler.sample(any(), anyInt())).thenReturn(hitAndRunSamples);

        solver = mock(LinearProgramSolver.FACTORY.class);

        versionSpace = new LinearVersionSpace(sampler, solver);
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
    void sample_noIntercept_hitAndRunSamplerCalledWithCorrectParameters() {
        versionSpace.sample(trainingData, hitAndRunSamples.length);

        UnitBallPolyhedralCone cone = new UnitBallPolyhedralCone(Matrix.FACTORY.make(2, 1, 10, -20), solver);
        verify(sampler).sample(eq(cone), eq(hitAndRunSamples.length));
    }

    @Test
    void sample_withIntercept_hitAndRunSamplerCalledWithCorrectParameters() {
        versionSpace.addIntercept();
        versionSpace.sample(trainingData, hitAndRunSamples.length);

        UnitBallPolyhedralCone cone = new UnitBallPolyhedralCone(Matrix.FACTORY.make(2, 2, 1, 10, -1, -20), solver);
        verify(sampler).sample(eq(cone), eq(hitAndRunSamples.length));
    }

    @Test
    void sample_noIntercept_hitAndRunSamplesParsedAsExpected() {
        assertEquals(
                new LinearMajorityVote(Vector.FACTORY.zeros(hitAndRunSamples.length), Matrix.FACTORY.make(hitAndRunSamples)),
                versionSpace.sample(trainingData, hitAndRunSamples.length)
        );
    }

    @Test
    void sample_withIntercept_hitAndRunSamplesParsedAsExpected() {
        versionSpace.addIntercept();

        assertEquals(
                new LinearMajorityVote(Vector.FACTORY.make(1, 3, 5), Matrix.FACTORY.make(3, 1, 2, 4, 6)),
                versionSpace.sample(trainingData, hitAndRunSamples.length)
        );
    }

    @Test
    void name() {
        Matrix decompositionStore = Matrix.FACTORY.make(3, 3, 10, 0, 0, 5, -10, 0, -3, 5, 1);

        Matrix inverse = decompositionStore.copy();

        int n = decompositionStore.rows();
        for (int j = 0; j < n; j++) {
            inverse.set(j, j, 1 / inverse.get(j, j));

            for (int i = j + 1; i < n; i++) {
                double sum = 0;

                for (int r = j; r < i; r++) {
                    sum += inverse.get(i, r) * inverse.get(r, j);
                }

                inverse.set(i, j, -sum / inverse.get(i, i));
            }
        }

        System.out.println(decompositionStore);
        System.out.println(inverse);
        System.out.println(decompositionStore.matrixMultiply(inverse));
        System.out.println(inverse.matrixMultiply(decompositionStore));
    }
}