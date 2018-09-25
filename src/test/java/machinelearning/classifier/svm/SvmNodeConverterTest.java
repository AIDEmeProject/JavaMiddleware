package machinelearning.classifier.svm;

import data.DataPoint;
import libsvm.svm_node;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SvmNodeConverterTest {

    private double[] values;

    @Test
    void toSvmNode_ReceivesNonEmptyArray_CorrectConversion() {
        values = new double[] {-3, 10};
        svm_node[] result = SvmNodeConverter.toSvmNode(values);

        assertEquals(0, result[0].index);
        assertEquals(-3, result[0].value);
        assertEquals(1, result[1].index);
        assertEquals(10, result[1].value);
    }

    @Test
    void toSvmNode_ReceivesEmptyArray_returnsEmptyArray() {
        values = new double[0];
        svm_node[] result =  SvmNodeConverter.toSvmNode(values);
        assertEquals(0, result.length);
    }

    @Test
    void toDataPoint_emptyInputMatrix_returnsEmptyArray() {
        assertTrue(SvmNodeConverter.toDataPoint(new svm_node[0][]).isEmpty());
    }

    @Test
    void toDataPoint_nonEmptyInputMatrix_correctConversion() {
        svm_node[][] nodes = new svm_node[][] {
                {createSvmNode(0, 10), createSvmNode(1, 20)},
                {createSvmNode(0, -10), createSvmNode(1, -20)}
        };

        List<DataPoint> points = SvmNodeConverter.toDataPoint(nodes);
        assertEquals(2, points.size());
        assertEquals(new DataPoint(0, new double[] {10, 20}), points.get(0));
        assertEquals(new DataPoint(1, new double[] {-10, -20}), points.get(1));
    }

    private svm_node createSvmNode(int index, double value) {
        svm_node node = new svm_node();
        node.index = index;
        node.value = value;
        return node;
    }
}