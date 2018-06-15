package classifier.SVM;

import classifier.Learner;
import data.LabeledDataset;
import data.LabeledPoint;
import exceptions.EmptyLabeledSetException;
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

    /**
     * SVM training parameters
     */
    private final SvmParameterAdapter parameter;

    // Disables libsvm's training output
    static {
        svm.svm_set_print_string_function(s -> {});
    }

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
    public SvmClassifier fit(LabeledDataset data) {
        if (data.getNumLabeledRows() == 0){
            throw new EmptyLabeledSetException();
        }

        svm_problem prob = buildSvmProblem(data);
        svm_parameter param = parameter.build();

        // use default gamma if needed
        if(param.gamma <= 0){
            param.gamma = 1.0 / prob.x[0].length;
        }

        return new SvmClassifier(svm.svm_train(prob, param));
    }

    private svm_problem buildSvmProblem(LabeledDataset data){
        svm_problem prob = new svm_problem();

        prob.l = data.getNumLabeledRows();
        prob.x = new svm_node[prob.l][];
        prob.y = new double[prob.l];

        int i = 0;
        for (LabeledPoint point : data.getLabeledPoints()) {
            prob.x[i] = SvmNodeConverter.toSvmNodeArray(point.getData());
            prob.y[i++] = point.getLabel();
        }

        return prob;
    }
}
