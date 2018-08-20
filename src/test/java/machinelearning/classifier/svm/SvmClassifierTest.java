package machinelearning.classifier.svm;

import data.DataPoint;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SvmClassifierTest {
    private SvmClassifier classifier;

    @BeforeEach
    void setUp() {
        double bias = 0.0;
        double[] alpha = new double[] {1,-1};
        double[][] supportVectors = new double[][] {{1,0},{0,1}};
        Kernel kernel = new Kernel().kernelType(KernelType.LINEAR);
        classifier = new SvmClassifier(bias, alpha, kernel, supportVectors);
    }

    @Test
    void margin_ComputeMarginOfAttributeValueArray_CorrectMarginComputed() {
        DataPoint point = new DataPoint(0, new double[] {0,1});
        assertEquals(-1, classifier.margin(point));
    }

    @Test
    void margin_PredictLabelOfAttributeValueArray_ExpectedLabelPredicted() {
        DataPoint point = new DataPoint(0, new double[] {0,1});
        assertEquals(Label.NEGATIVE, classifier.predict(point));
    }
}