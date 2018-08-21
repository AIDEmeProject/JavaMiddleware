package machinelearning.classifier.svm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearKernelTest extends AbstractKernelTest {
    @BeforeEach
    void setUp() {
        kernel = new LinearKernel();
    }

    @Test
    void compute_validInputs_returnsExpectedValue() {
        assertKernelFunctionIsCorrect(4, new double[]{1, 2}, new double[]{-2, 3});
    }

    @Test
    void computeKernelMatrix_validInput_returnsExpectedValue() {
        double[][] toCompute = new double[][] {{1, 2}, {-2, 3}};
        double[][] expected = new double[][] {{5, 4}, {4, 13}};
        assertKernelMatrixIsCorrectlyComputed(expected, toCompute);
    }
}