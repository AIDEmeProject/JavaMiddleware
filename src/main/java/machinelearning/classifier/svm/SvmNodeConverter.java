package machinelearning.classifier.svm;

import libsvm.svm_node;


/**
 * This utility class is responsible for converting to and from svm_node objects used in LibSVM class.
 *
 * @author luciano
 */
class SvmNodeConverter {

    /**
     * Convert a double matrix into a svm_node matrix
     * @param x: matrix to be converted
     * @return converted matrix
     */
    static svm_node[] toSvmNodeArray(double[] x){
        svm_node[] converted = new svm_node[x.length];

        for (int i = 0; i < x.length; i++) {
            converted[i] = new svm_node();
            converted[i].index = i;
            converted[i].value = x[i];
        }

        return converted;
    }

    /**
     * Convert a double matrix into a svm_node matrix
     * @param x: matrix to be converted
     * @return converted matrix
     */
    static svm_node[][] toSvmNodeArray(double[][] x){
        svm_node[][] converted = new svm_node[x.length][x[0].length];

        for (int i = 0; i < x.length; i++) {
            converted[i] = toSvmNodeArray(x[i]);
        }

        return converted;
    }
}