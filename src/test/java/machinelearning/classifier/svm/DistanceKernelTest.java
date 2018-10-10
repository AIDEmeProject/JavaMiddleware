package machinelearning.classifier.svm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DistanceKernelTest extends AbstractKernelTest {
    @BeforeEach
    void setUp() {
        kernel = new DistanceKernel();
    }

    @Test
    void compute_validInputs_returnsExpectedValue() {
        assertKernelFunctionIsCorrect(10, new double[]{1, 2}, new double[]{-2, 3});
    }

    @Test
    void computeKernelMatrix_validInput_returnsExpectedValue() {
        double[][] toCompute = new double[][] {{1, 2}, {-2, 3}};
        double[][] expected = new double[][] {{0, 10}, {10, 0}};
        assertKernelMatrixIsCorrectlyComputed(expected, toCompute);
    }
}
