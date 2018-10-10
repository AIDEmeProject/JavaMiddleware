package machinelearning.classifier.margin;

import machinelearning.classifier.svm.Kernel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class KernelClassifierTest {
    private Matrix support;
    private Kernel kernel;
    private KernelClassifier classifier;

    @BeforeEach
    void setUp() {
        kernel = spy(Kernel.class);
        when(kernel.compute((Vector) any(), any())).thenReturn(1.);
        support = Matrix.FACTORY.make(3, 2, 1, 1, 2, 3, 4, 5);
        classifier = new KernelClassifier(1, Vector.FACTORY.make(-2,3,0), support, kernel);
    }

    @Test
    void linearClassifierConstructor_NullLinearClassifier_throwsException() {
        assertThrows(NullPointerException.class, () -> new KernelClassifier(null, support, kernel));
    }

    @Test
    void linearClassifierConstructor_nullKernel_throwsException() {
        assertThrows(NullPointerException.class,
                () -> new KernelClassifier(mock(LinearClassifier.class), support, null));
    }

    @Test
    void linearClassifierConstructor_differentLinearClassifierDimensionAndSupportVector_throwsException() {
        LinearClassifier linearClassifier = mock(LinearClassifier.class);
        when(linearClassifier.getDim()).thenReturn(2);

        assertThrows(IllegalArgumentException.class,
                () -> new KernelClassifier(linearClassifier, mock(Matrix.class), kernel));
    }

    @Test
    void margin_alwaysOneKernel_returnsCorrectMargin() {
        assertEquals(2, classifier.margin(Vector.FACTORY.make(1, 2)));
    }
}