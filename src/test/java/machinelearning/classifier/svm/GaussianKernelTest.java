package machinelearning.classifier.svm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GaussianKernelTest extends AbstractKernelTest {
    @BeforeEach
    void setUp() {
        kernel = new GaussianKernel();
    }

    @Test
    void gammaConstructor_negativeGamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GaussianKernel(-1));
    }


    @Test
    void gammaConstructor_zeroGamma_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GaussianKernel(0));
    }

    @Test
    void compute_gammaEqualsToTwo_returnsExpectedValue() {
        kernel = new GaussianKernel(2);
        assertKernelFunctionIsCorrect(Math.exp(-20), new double[]{1, 2}, new double[]{-2, 3});
    }

    @Test
    void compute_defaultGamma_returnsExpectedValue() {
        assertKernelFunctionIsCorrect(Math.exp(-5), new double[]{1, 2}, new double[]{-2, 3});
    }

    @Test
    void computeKernelMatrix_gammaEqualsToTwo_returnsExpectedValue() {
        kernel = new GaussianKernel(2);
        double[][] toCompute = new double[][] {{1, 2}, {-2, 3}};
        double[][] expected = new double[][] {{1, Math.exp(-20)}, {Math.exp(-20), 1}};
        assertKernelMatrixIsCorrectlyComputed(expected, toCompute);
    }

    @Test
    void computeKernelMatrix_defaultGamma_returnsExpectedValue() {
        double[][] toCompute = new double[][] {{1, 2}, {-2, 3}};
        double[][] expected = new double[][] {{1, Math.exp(-5)}, {Math.exp(-5), 1}};
        assertKernelMatrixIsCorrectlyComputed(expected, toCompute);
    }
}