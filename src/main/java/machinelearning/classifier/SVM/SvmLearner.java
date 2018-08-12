package machinelearning.classifier.SVM;

import machinelearning.classifier.Learner;
import data.LabeledPoint;
import libsvm.svm;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import utils.Validator;

import java.util.Collection;

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
     * @param labeledPoints: labeled data
     * @return SVM classifier instance
     */
    @Override
    public SvmClassifier fit(Collection<LabeledPoint> labeledPoints) {
        svm_problem prob = buildSvmProblem(labeledPoints);
        svm_parameter param = parameter.build();

        // use default gamma if needed
        if(param.gamma <= 0){
            param.gamma = 1.0 / prob.x[0].length;
        }

        return new SvmClassifier(svm.svm_train(prob, param));
    }

    private svm_problem buildSvmProblem(Collection<LabeledPoint> labeledPoints){
        Validator.assertNotEmpty(labeledPoints);

        svm_problem prob = new svm_problem();

        prob.l = labeledPoints.size();
        prob.x = new svm_node[prob.l][];
        prob.y = new double[prob.l];

        int i = 0;
        for (LabeledPoint point : labeledPoints) {
            prob.x[i] = SvmNodeConverter.toSvmNodeArray(point.getData());
            prob.y[i] = point.getLabel().asBinary();
            i++;
        }

        return prob;
    }
}
