package machinelearning.classifier.svm;

import data.LabeledPoint;
import machinelearning.classifier.AbstractLearnerTest;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.margin.KernelClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        List<LabeledPoint> labeledPoints = buildTrainingSet(
                new double[][]{{-1, 0}, {1, 0}}, new Label[]{Label.NEGATIVE, Label.POSITIVE});

        Classifier classifier = learner.fit(labeledPoints);

        for (LabeledPoint point : labeledPoints) {
            assertEquals(point.getLabel(), classifier.predict(point));
        }
    }

    @Test
    void fit_twoPointsTrainingSet_fittedClassifierHasExpectedMargin() {
        List<LabeledPoint> labeledPoints = buildTrainingSet(
                new double[][]{{-1, 0}, {1, 0}}, new Label[]{Label.NEGATIVE, Label.POSITIVE});

        KernelClassifier classifier = (KernelClassifier) learner.fit(labeledPoints);

        for (LabeledPoint point : labeledPoints) {
            assertEquals(point.getLabel().asSign() * 1.0, classifier.margin(point.getData()));
        }
    }

    private List<LabeledPoint> buildTrainingSet(double[][] x, Label[] y) {
        List<LabeledPoint> labeledPoints = new ArrayList<>();

        for (int i = 0; i < x.length; i++) {
            labeledPoints.add(new LabeledPoint(i, x[i], y[i]));
        }

        return labeledPoints;
    }
}