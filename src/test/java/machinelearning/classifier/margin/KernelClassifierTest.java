package machinelearning.classifier.margin;

import data.DataPoint;
import machinelearning.classifier.svm.Kernel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KernelClassifierTest {
    private Collection<DataPoint> support;
    private Kernel kernel;
    private KernelClassifier classifier;

    @BeforeEach
    void setUp() {
        kernel = mock(Kernel.class);

        support = new ArrayList<>();
        support.add(new DataPoint(0, new double[] {1,1}));
        support.add(new DataPoint(1, new double[] {2,3}));

        classifier = new KernelClassifier(1, new double[] {-2,3}, support, kernel);
    }

    @Test
    void linearClassifierConstructor_NullLinearClassifier_throwsException() {
        assertThrows(NullPointerException.class, () -> new KernelClassifier(null, support, kernel));
    }

    @Test
    void linearClassifierConstructor_emptySupportVectors_throwsException() {
        LinearClassifier linearClassifier = mock(LinearClassifier.class);
        when(linearClassifier.getDim()).thenReturn(2);

        assertThrows(IllegalArgumentException.class,
                () -> new KernelClassifier(linearClassifier, Collections.EMPTY_LIST, kernel));
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

        support.add(mock(DataPoint.class));

        assertThrows(IllegalArgumentException.class,
                () -> new KernelClassifier(linearClassifier, support, kernel));
    }

    @Test
    void margin_alwaysOneKernel_returnsCorrectMargin() {
        when(kernel.compute((Collection<? extends DataPoint>) any(), (Vector) any())).thenReturn(new double[] {1, 1});
        assertEquals(2, classifier.margin(Vector.FACTORY.make(1, 2)));
    }
}