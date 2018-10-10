package machinelearning.classifier.svm;

import data.LabeledDataset;
import data.LabeledPoint;
import machinelearning.classifier.AbstractLearnerTest;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.margin.KernelClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SvmLearnerTest extends AbstractLearnerTest {

    @BeforeEach
    void setUp() {
        learner = new SvmLearner(1, new LinearKernel());
    }

    @Test
    void fit_twoPointsTrainingSet_pointsCorrectlyClassifier() {
        LabeledDataset labeledPoints = buildTrainingSet(
                new double[][]{{-1, 0}, {1, 0}}, new Label[]{Label.NEGATIVE, Label.POSITIVE});

        Classifier classifier = learner.fit(labeledPoints);

        for (LabeledPoint point : labeledPoints) {
            assertEquals(point.getLabel(), classifier.predict(point));
        }
    }

    @Test
    void fit_twoPointsTrainingSet_fittedClassifierHasExpectedMargin() {
        LabeledDataset labeledPoints = buildTrainingSet(
                new double[][]{{-1, 0}, {1, 0}}, new Label[]{Label.NEGATIVE, Label.POSITIVE});

        KernelClassifier classifier = (KernelClassifier) learner.fit(labeledPoints);

        for (LabeledPoint point : labeledPoints) {
            assertEquals(point.getLabel().asSign() * 1.0, classifier.margin(point.getData()));
        }
    }

    private LabeledDataset buildTrainingSet(double[][] x, Label[] y) {
        List<Long> indexes = new ArrayList<>();

        for (long i = 0; i < x.length; i++) {
            indexes.add(i);
        }

        return new LabeledDataset(indexes, Matrix.FACTORY.make(x), y);
    }
}