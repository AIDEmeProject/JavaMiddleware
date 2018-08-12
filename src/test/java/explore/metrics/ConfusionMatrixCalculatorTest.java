package explore.metrics;

import machinelearning.classifier.Label;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfusionMatrixCalculatorTest {

    private ConfusionMatrixCalculator calculator = new ConfusionMatrixCalculator();

    @Test
    void compute_ZeroLengthLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(new Label[0], new Label[0]));
    }

    @Test
    void compute_IncompatibleSizesLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->  calculator.compute(new Label[2], new Label[4]));
    }

    @Test
    void compute_allLabelsCorrectlyPredicted_confusionMatrixCorrectlyComputed() {
        Label[] labels = {Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE};
        ConfusionMatrix metric = calculator.compute(labels, labels);
        assertEquals(2, metric.truePositives());
        assertEquals(3, metric.trueNegatives());
        assertEquals(0, metric.falsePositives());
        assertEquals(0, metric.falseNegatives());
    }

    @Test
    void compute_allLabelsWronglyPredicted_confusionMatrixCorrectlyComputed() {
        Label[] predictedLabels = {Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE};
        Label[] trueLabels = {Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE};
        ConfusionMatrix metric = calculator.compute(trueLabels, predictedLabels);
        assertEquals(0, metric.truePositives());
        assertEquals(0, metric.trueNegatives());
        assertEquals(3, metric.falsePositives());
        assertEquals(2, metric.falseNegatives());
    }

    @Test
    void compute_predictedLabelsPartiallyCorrect_confusionMatrixCorrectlyComputed() {
        Label[] predictedLabels = {Label.NEGATIVE, Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE};
        Label[] trueLabels = {Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE};
        ConfusionMatrix metric = calculator.compute(trueLabels, predictedLabels);
        assertEquals(1, metric.truePositives());
        assertEquals(2, metric.trueNegatives());
        assertEquals(1, metric.falsePositives());
        assertEquals(1, metric.falseNegatives());
    }
}
