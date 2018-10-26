package machinelearning.classifier.svm;

import data.LabeledDataset;
import data.LabeledPoint;
import libsvm.*;
import machinelearning.classifier.Learner;
import machinelearning.classifier.margin.KernelClassifier;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * This module is responsible for training an SVM classifier over labeled data. Basically, it is a wrapper over the
 * well known libsvm library.
 *
 * <a href="https://www.csie.ntu.edu.tw/">libsvm homepage</a>
 * <a href="https://www.csie.ntu.edu.tw/~r94100/libsvm-2.8/README">svm training README</a>
 */
public class SvmLearner implements Learner {

    /**
     * SVM's penalty value
     */
    private final double C;

    /**
     * SVM kernel function
     */
    private final Kernel kernel;

    // Disable libsvm training output
    static {
        svm.svm_set_print_string_function(s -> {});
    }

    /**
     * @param C: penalty parameter
     * @param kernel: kernel function
     */
    public SvmLearner(double C, Kernel kernel) {
        Validator.assertPositive(C);
        Validator.assertNotNull(kernel);

        this.C = C;
        this.kernel = kernel;
    }

    private svm_parameter buildSvmParameter() {
        svm_parameter parameter = new svm_parameter();

        parameter.C = C;
        kernel.setSvmParameters(parameter);

        // probability estimates (no estimation by default)
        parameter.probability = 0;

        // DO NOT CHANGE THESE PARAMETERS
        parameter.svm_type = svm_parameter.C_SVC;
        parameter.cache_size = 40;  // cache size in MB
        parameter.eps = 1e-3; // optimization tolerance
        parameter.shrinking = 1;  // use shrinking optimization
        parameter.nr_weight = 0;  // no class weights
        parameter.weight = new double[0];

        return parameter;
    }

    /**
     * Trains a SVM classifier over the labeled data.
     * @param labeledPoints: labeled data
     * @return fitted SVM model as a KernelClassifier instance
     */
    @Override
    public KernelClassifier fit(LabeledDataset labeledPoints) {
        svm_parameter param = buildSvmParameter();
        svm_problem prob = buildSvmProblem(labeledPoints);
        svm_parameter parameter = (svm_parameter) param.clone();

        // use default gamma if needed
        if(parameter.gamma <= 0){
            parameter.gamma = 1.0 / prob.x[0].length;
        }

        return parseKernelClassifierFromSvmModel(svm.svm_train(prob, parameter), labeledPoints.dim());

    }

    private svm_problem buildSvmProblem(LabeledDataset labeledPoints){
        svm_problem prob = new svm_problem();

        prob.l = labeledPoints.length();
        prob.x = new svm_node[prob.l][];
        prob.y = new double[prob.l];

        int i = 0;
        for (LabeledPoint point : labeledPoints) {
            prob.x[i] = SvmNodeConverter.toSvmNode(point.getData());
            prob.y[i] = point.getLabel().asSign();
            i++;
        }

        return prob;
    }

    private KernelClassifier parseKernelClassifierFromSvmModel(svm_model model, int dim) {
        double bias = -model.rho[0];
        Vector alpha = Vector.FACTORY.make(model.sv_coef[0]);
        Matrix sv = SvmNodeConverter.toMatrix(model.SV, dim);

        return new KernelClassifier(bias, alpha, sv, kernel);
    }

    @Override
    public String toString() {
        return "SVM C = " + C + ", kernel = " + kernel;
    }
}
