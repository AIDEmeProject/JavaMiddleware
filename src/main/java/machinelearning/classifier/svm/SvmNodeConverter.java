package machinelearning.classifier.svm;

import data.DataPoint;
import libsvm.svm_node;

import java.util.ArrayList;
import java.util.List;


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
    static svm_node[] toSvmNode(double[] x){
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
    static svm_node[][] toSvmNode(double[][] x){
        svm_node[][] converted = new svm_node[x.length][x[0].length];

        for (int i = 0; i < x.length; i++) {
            converted[i] = toSvmNode(x[i]);
        }

        return converted;
    }

    /**
     * Convert a double array into a svm_node array
     * @param nodes: matrix to be converted
     * @return converted matrix
     */
    private static DataPoint toDataPoint(int index, svm_node[] nodes){
        double[] converted = new double[nodes.length];

        for (int i = 0; i < nodes.length; i++) {
            converted[i] = nodes[i].value;
        }

        return new DataPoint(index, converted);
    }

    /**
     * Convert a svm_node matrix into a list of DataPoints
     * @param nodes: matrix to be converted
     * @return converted matrix
     */
    static List<DataPoint> toDataPoint(svm_node[][] nodes){
        List<DataPoint> converted = new ArrayList<>(nodes.length);

        for (int i = 0; i < nodes.length; i++) {
            converted.add(toDataPoint(i, nodes[i]));
        }

        return converted;
    }
}