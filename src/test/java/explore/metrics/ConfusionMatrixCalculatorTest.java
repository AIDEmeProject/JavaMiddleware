package explore.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfusionMatrixCalculatorTest {

    private ConfusionMatrixCalculator calculator = new ConfusionMatrixCalculator();

    @Test
    void compute_LabelDifferentFromZeroOrOne_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(new int[] {1,0}, new int[] {-1,1}));
    }

    @Test
    void compute_ZeroLengthLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(new int[0], new int[0]));
    }

    @Test
    void compute_IncompatibleSizesLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->  calculator.compute(new int[2], new int[4]));
    }

    @Test
    void compute_allLabelsCorrectlyPredicted_confusionMatrixCorrectlyComputed() {
        int[] labels = {0,1,0,1,0};
        ConfusionMatrix metric = calculator.compute(labels, labels);
        assertEquals(2, metric.truePositives());
        assertEquals(3, metric.trueNegatives());
        assertEquals(0, metric.falsePositives());
        assertEquals(0, metric.falseNegatives());
    }

    @Test
    void compute_allLabelsWronglyPredicted_confusionMatrixCorrectlyComputed() {
        int[] predictedLabels = {1,0,1,0,1};
        int[] trueLabels      = {0,1,0,1,0};
        ConfusionMatrix metric = calculator.compute(trueLabels, predictedLabels);
        assertEquals(0, metric.truePositives());
        assertEquals(0, metric.trueNegatives());
        assertEquals(3, metric.falsePositives());
        assertEquals(2, metric.falseNegatives());
    }

    @Test
    void compute_predictedLabelsPartiallyCorrect_confusionMatrixCorrectlyComputed() {
        int[] predictedLabels = {0,0,1,1,0};
        int[] trueLabels      = {0,1,0,1,0};
        ConfusionMatrix metric = calculator.compute(trueLabels, predictedLabels);
        assertEquals(1, metric.truePositives());
        assertEquals(2, metric.trueNegatives());
        assertEquals(1, metric.falsePositives());
        assertEquals(1, metric.falseNegatives());
    }
}
