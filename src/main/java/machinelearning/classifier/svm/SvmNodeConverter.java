package machinelearning.classifier.svm;

import libsvm.svm_node;
import utils.linalg.Matrix;
import utils.linalg.Vector;


/**
 * This utility class is responsible for converting to and from svm_node objects used in LibSVM class.
 *
 * @author luciano
 */
class SvmNodeConverter {

    /**
     * Convert a double array into a svm_node array
     * @param x: matrix to be converted
     * @return converted matrix
     */
    static svm_node[] toSvmNode(Vector x) {
        svm_node[] converted = new svm_node[x.dim()];

        for (int i = 0; i < x.dim(); i++) {
            converted[i] = new svm_node();
            converted[i].index = i;
            converted[i].value = x.get(i);
        }

        return converted;
    }

    /**
     * Convert a svm_node matrix into a list of DataPoints
     * @param nodes: matrix to be converted
     * @return converted matrix
     */
    static Matrix toMatrix(svm_node[][] nodes, int dim){
        if (nodes.length == 0) {
            throw new IllegalArgumentException("Cannot convert empty node collection.");
        }

        double[][] converted = new double[nodes.length][dim];

        for (int i = 0; i < nodes.length; i++) {
            for (svm_node node : nodes[i]) {
                converted[i][node.index] = node.value;
            }
        }

        return Matrix.FACTORY.make(converted);
    }
}