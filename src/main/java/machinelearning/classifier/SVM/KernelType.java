package machinelearning.classifier.SVM;

import libsvm.svm_parameter;

/**
 * Enumeration of all choices of kernels available in LibSVM. The corresponding values are the same as the ones in the library.
 *
 * Precomputed or user-defined kernels are not supported right now.
 *
 * @author luciano
 */
public enum KernelType {
    LINEAR(svm_parameter.LINEAR), POLY(svm_parameter.POLY), RBF(svm_parameter.RBF), SIGMOID(svm_parameter.SIGMOID);

    /**
     * LibSvm kernel id
     */
    private int id;

    KernelType(int id){
        this.id = id;
    }

    /**
     * @return LibSVM kernel id
     */
    public int getId(){
        return id;
    }
}
