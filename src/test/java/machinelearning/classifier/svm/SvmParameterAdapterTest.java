package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SvmParameterAdapterTest {

    private SvmParameterAdapter param;

    @BeforeEach
    void setUp() {
        param = new SvmParameterAdapter();
    }

    @Test
    void C_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> param.C(-1));
    }

    @Test
    void cacheSize_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> param.cacheSize(-1));
    }

    @Test
    void tolerance_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> param.tolerance(-1));
    }

    @Test
    void classWeights_WrongDimension_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> param.classWeights(new double[]{1,2,3}));
    }

    @Test
    void classWeights_NegativeValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> param.classWeights(new double[]{1,-2}));
    }

    @Test
    void build_DefaultValues_SvmParameterObjectIsCorrectlyBuilt() {
        svm_parameter parameter = param.build();

        assertEquals(svm_parameter.C_SVC, parameter.svm_type);
        assertEquals(1.0, parameter.C);

        assertEquals(svm_parameter.RBF, parameter.kernel_type);
        assertEquals(0.0, parameter.gamma);
        assertEquals(0.0, parameter.coef0);
        assertEquals(3, parameter.degree);

        assertEquals(1e-3, parameter.eps);
        assertEquals(100, parameter.cache_size);
        assertEquals(1, parameter.shrinking);

        assertEquals(0, parameter.probability);

        assertArrayEquals(new double[0], parameter.weight);
        assertEquals(0, parameter.nr_weight);
        assertArrayEquals(new int[]{0,1}, parameter.weight_label);
    }

    @Test
    void build_NonDefaultValues_SvmParameterObjectIsCorrectlyBuilt() {
        param = param
                .C(2.0)
                .kernel(new Kernel().kernelType(KernelType.LINEAR))
                .cacheSize(200)
                .tolerance(1e-6)
                .shrinking(false)
                .probability(true)
                .classWeights(new double[] {2, 3});

        svm_parameter parameter = param.build();

        assertEquals(svm_parameter.C_SVC, parameter.svm_type);
        assertEquals(2.0, parameter.C);

        assertEquals(svm_parameter.LINEAR, parameter.kernel_type);

        assertEquals(1e-6, parameter.eps);
        assertEquals(200, parameter.cache_size);
        assertEquals(0, parameter.shrinking);

        assertEquals(1, parameter.probability);

        assertArrayEquals(new double[] {2, 3}, parameter.weight);
        assertEquals(2, parameter.nr_weight);
        assertArrayEquals(new int[]{0,1}, parameter.weight_label);
    }
}