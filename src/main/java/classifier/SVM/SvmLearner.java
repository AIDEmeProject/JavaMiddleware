package classifier.SVM;

import classifier.Classifier;
import classifier.Learner;
import data.LabeledData;
import libsvm.svm;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * This module is responsible for training an SVM classifier over labeled data.
 *
 * @author luciano
 */
public class SvmLearner implements Learner {

    private SvmParameterAdapter parameter;

    /**
     * Builds SvmLearner from SvmParameterAdapter instance.
     * @param param: svm parameter instance
     */
    public SvmLearner(SvmParameterAdapter param) {
        this.parameter = param;
    }

    /**
     * Trains a SVM classifier over the labeled data.
     * @param data: labeled data
     * @return SVM classifier instance
     */
    @Override
    public Classifier fit(LabeledData data) {
        svm_problem prob = buildSvmProblem(data);
        svm_parameter param = parameter.build();

        // use default gamma if needed
        if(param.gamma <= 0){
            param.gamma = 1.0 / prob.x[0].length;
        }

        return new SvmClassifier(svm.svm_train(prob, param));
    }

    private svm_problem buildSvmProblem(LabeledData data){
        svm_problem prob = new svm_problem();

        prob.l = data.getNumLabeledRows();
        prob.x = new svm_node[prob.l][];
        prob.y = new double[prob.l];

        int i = 0;
        for (Integer row : data.getLabeledRows()) {
            prob.x[i] = SvmNodeConverter.toSvmNodeArray(data.getRow(row));
            prob.y[i] = data.getLabel(row);
            i++;
        }

        return prob;
    }
}
