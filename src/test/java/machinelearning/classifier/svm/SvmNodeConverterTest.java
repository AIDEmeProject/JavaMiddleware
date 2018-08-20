package machinelearning.classifier.svm;

import libsvm.svm_node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SvmNodeConverterTest {

    private double[] values;

    @Test
    void toSvmNodeArray_ReceivesNonEmptyArray_CorrectConversion() {
        values = new double[] {-3, 10};
        svm_node[] result = SvmNodeConverter.toSvmNodeArray(values);

        assertEquals(0, result[0].index);
        assertEquals(-3, result[0].value);
        assertEquals(1, result[1].index);
        assertEquals(10, result[1].value);
    }

    @Test
    void toSvmNodeArray_ReceivesEmptyArray_EmptyArray() {
        values = new double[0];
        svm_node[] result =  SvmNodeConverter.toSvmNodeArray(values);
        assertEquals(0, result.length);
    }

    @Test
    void toSvmNodeArray_ReceivesNonEmptyDoubleMatrix_CorrectConversion() {
        double[][] values = new double[][] {{10,20},{-10,-20}};

        svm_node[][] result = SvmNodeConverter.toSvmNodeArray(values);

        assertEquals(result[0][0].index, 0);
        assertEquals(result[0][0].value, 10);
        assertEquals(result[0][1].index, 1);
        assertEquals(result[0][1].value, 20);

        assertEquals(result[1][0].index, 0);
        assertEquals(result[1][0].value, -10);
        assertEquals(result[1][1].index, 1);
        assertEquals(result[1][1].value, -20);
    }
}