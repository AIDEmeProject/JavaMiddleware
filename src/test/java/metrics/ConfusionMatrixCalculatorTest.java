package metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfusionMatrixCalculatorTest {

    private ConfusionMatrixCalculator calculator = new ConfusionMatrixCalculator();

    @Test
    void compute_LabelDifferentFrom0or1_ThrowsException() {
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

}
