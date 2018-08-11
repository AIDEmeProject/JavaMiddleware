package machinelearning.classifier.SVM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KernelTest {
    private Kernel kernel;
    private double[] x, y;

    @BeforeEach
    void setUp() {
        kernel = new Kernel();
        x = new double[] {-2,0,2};
        y = new double[] {-1,1,0};
    }

    @Test
    void gamma_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> kernel.gamma(-1));
    }

    @Test
    void coef0_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> kernel.coef0(-1));
    }

    @Test
    void coef0_ZeroValue_Passes() {
        kernel.coef0(0);
    }

    @Test
    void degree_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> kernel.degree(-1));
    }

    @Test
    void getters_DefaultValues_RbfKernelConstructed() {
        assertEquals(KernelType.RBF, kernel.getKernelType());
        assertEquals(0, kernel.getGamma());
        assertEquals(0, kernel.getCoef0());
        assertEquals(3, kernel.getDegree());
    }

    @Test
    void getters_CustomKernelValues_ExpectedKernelConstructed() {
        kernel = kernel
                .kernelType(KernelType.POLY)
                .gamma(1)
                .degree(5)
                .coef0(10);
        assertEquals(KernelType.POLY, kernel.getKernelType());
        assertEquals(1, kernel.getGamma());
        assertEquals(10, kernel.getCoef0());
        assertEquals(5, kernel.getDegree());
    }

    @Test
    void compute_DifferentNumberOfDimensions_ThrowsException() {
        y = new double[] {1,1};
        assertThrows(IllegalArgumentException.class, () -> kernel.compute(x,y));
    }

    @Test
    void compute_LinearKernel_ComputedExpectedValue() {
        kernel = kernel.kernelType(KernelType.LINEAR);

        assertEquals(2, kernel.compute(x,y));
    }

    @Test
    void compute_PolyKernel_ComputedExpectedValue() {
        kernel = kernel
                .kernelType(KernelType.POLY)
                .gamma(3)
                .coef0(1)
                .degree(2);

        assertEquals(49, kernel.compute(x,y));
    }

    @Test
    void compute_RbfKernel_ComputedExpectedValue() {
        kernel = kernel
                .kernelType(KernelType.RBF)
                .gamma(2.0);

        assertEquals(Math.exp(-12), kernel.compute(x,y));
    }

    @Test
    void compute_RbfKernelWithDefaultGamma_ComputedExpectedValue() {
        kernel = kernel
                .kernelType(KernelType.RBF);

        assertEquals(Math.exp(-2), kernel.compute(x,y));
    }

    @Test
    void compute_SigmoidKernel_ComputedExpectedValue() {
        kernel = kernel
                .gamma(3)
                .coef0(1)
                .kernelType(KernelType.SIGMOID);

        assertEquals(Math.tanh(7), kernel.compute(x,y));
    }
}