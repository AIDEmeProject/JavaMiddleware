package benchmark;

import data.IndexedDataset;
import data.LabeledDataset;
import machinelearning.active.learning.versionspace.KernelVersionSpace;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.manifold.selector.WarmUpAndThinSelector;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import machinelearning.classifier.MajorityVoteLearner;
import machinelearning.classifier.svm.GaussianKernel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import utils.linalg.Matrix;
import utils.linprog.LinearProgramSolver;

import java.util.ArrayList;
import java.util.List;

public class ClassifierBenchmark extends AbstractBenchmark {
    private Matrix testData;
    private Classifier classifier;

    @Setup(Level.Trial)
    public void doSetup() {
        classifier = getClassifier(size);
        testData = getRandomMatrix(10000, 2, 7568);
    }

    private Classifier getClassifier(int trainingSetSize) {
        Matrix trainingData = getRandomMatrix(trainingSetSize, 2, 4444);

        Label[] labels = new Label[trainingSetSize];
        for (int i = 0; i < trainingSetSize; i++) {
            labels[i] = i < trainingSetSize / 2 ? Label.POSITIVE : Label.NEGATIVE;
        }

        Learner learner = new MajorityVoteLearner(
                new KernelVersionSpace(
                        new LinearVersionSpace(
                                new HitAndRunSampler(
                                        new RandomDirectionAlgorithm(),
                                        new WarmUpAndThinSelector(64, 1),
                                        new SampleCache()
                                ),
                                LinearProgramSolver.getFactory(LinearProgramSolver.LIBRARY.OJALGO)
                        ),
                        new GaussianKernel()
                ),
                8
        );

        List<Long> indexes = new ArrayList<>(trainingSetSize);
        for (long i = 0; i < trainingSetSize; i++) {
            indexes.add(i);
        }

        return learner.fit(new LabeledDataset(new IndexedDataset(indexes, trainingData), labels));
    }

    @Benchmark
    public Label[] predict() {
        return classifier.predict(testData);
    }
}
