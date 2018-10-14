package machinelearning.classifier.svm;

import libsvm.svm_node;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SvmNodeConverterTest {

    @Test
    void toSvmNode_ReceivesNonEmptyArray_CorrectConversion() {
        Vector values = Vector.FACTORY.make( -3, 10);
        svm_node[] result = SvmNodeConverter.toSvmNode(values);

        assertEquals(0, result[0].index);
        assertEquals(-3, result[0].value);
        assertEquals(1, result[1].index);
        assertEquals(10, result[1].value);
    }

    @Test
    void toMatrix_emptyInputArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> SvmNodeConverter.toMatrix(new svm_node[0][], 0));
    }

    @Test
    void toMatrix_nonEmptyInputArray_correctConversion() {
        svm_node[][] nodes = new svm_node[][] {
                {createSvmNode(0, 10), createSvmNode(1, 20)},
                {createSvmNode(0, -10), createSvmNode(1, -20)}
        };

        Matrix points = SvmNodeConverter.toMatrix(nodes, 2);
        assertEquals(Matrix.FACTORY.make(2, 2, 10, 20, -10, -20), points);
    }

    private svm_node createSvmNode(int index, double value) {
        svm_node node = new svm_node();
        node.index = index;
        node.value = value;
        return node;
    }
}