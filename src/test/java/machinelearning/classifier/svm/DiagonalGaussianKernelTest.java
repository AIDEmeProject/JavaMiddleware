package machinelearning.classifier.svm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiagonalGaussianKernelTest extends AbstractKernelTest {
    private Vector diagonal;

    @BeforeEach
    void setUp() {
        diagonal = Vector.FACTORY.make(2.0, 3.0);
        kernel = new DiagonalGaussianKernel(diagonal);
    }

    @Test
    void constructor_negativeValueInDiagonal_throwsException() {
        diagonal = Vector.FACTORY.make(-1);
        assertThrows(IllegalArgumentException.class, () -> new DiagonalGaussianKernel(diagonal));
    }


    @Test
    void constructor_zeroValueInDiagonal_throwsException() {
        diagonal = Vector.FACTORY.make(0);
        assertThrows(IllegalArgumentException.class, () -> new DiagonalGaussianKernel(diagonal));
    }

    @Test
    void compute_gammaEqualsToTwo_returnsExpectedValue() {
        assertKernelFunctionIsCorrect(Math.exp(-21), new double[]{1, 2}, new double[]{-2, 3});
    }

    @Test
    void computeKernelMatrix_gammaEqualsToTwo_returnsExpectedValue() {
        double[][] toCompute = new double[][] {{1, 2}, {-2, 3}};
        double[][] expected = new double[][] {{1, Math.exp(-21)}, {Math.exp(-21), 1}};
        assertKernelMatrixIsCorrectlyComputed(expected, toCompute);
    }
}
