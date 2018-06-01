package classifier.SVM;

import classifier.Classifier;

import data.LabeledData;
import libsvm.*;

/**
 *  This module represents a trained SVM classifier. It's an immutable object used for predicting labels over new data instances.
 *  The classifier is defined as:
 *          \( h(x) = sign ( b + \sum_i K(x_i, x) \alpha_i ) \)
 *
 *  We currently only support binary classification scenarios.
 *
 *  @author luciano
 */
public class SvmClassifier implements Classifier {
    private SvmParameterAdapter parameter;
    private svm_model model;
    static {
        // Disables svm output
        svm.svm_set_print_string_function(s -> {});
    }

    public SvmClassifier(SvmParameterAdapter parameter) {
        this.parameter = parameter;
    }

    /**
     * Instantiate SVM classifier from its parameters.
     * @param bias: bias parameter "b"
     * @param alpha: weight parameters
     * @param kernel: kernel function
     * @param supportVectors: collection of support vectors
     */
    public SvmClassifier(double bias, double[] alpha, Kernel kernel, double[][] supportVectors) {
        if(kernel.getGamma() == 0){
            kernel = kernel.gamma(1.0 / supportVectors[0].length);  // 1 / num_features
        }
        this.model = buildSvmModel(bias, alpha, kernel, supportVectors);
    }

//    @Override
//    public double predict(double[][] x) {
//        return svm.svm_predict(model, SvmNodeConverter.toSvmNodeArray(x));
//    }
//
//    public double predict(double[] x) {
//        return predict(x.getScaledAV());
//    }

    /**
     * @param sample: data point
     * @return sample's signed distance to svm's decision boundary
     */
    public double margin(double[] sample){
        double[] margin = new double[1];
        svm.svm_predict_values(model, SvmNodeConverter.toSvmNodeArray(sample), margin);
        return margin[0];
    }

    @Override
    public void fit(LabeledData data) {
        svm_problem prob = buildSvmProblem(data);
        svm_parameter param = parameter.build();

        // use default gamma if needed
        if(param.gamma <= 0){
            param.gamma = 1.0 / prob.x[0].length;
        }

        model = svm.svm_train(prob, param);
    }

    @Override
    public double probability(LabeledData data, int row) {
        return 0;
    }

    private svm_model buildSvmModel(double bias, double[] alpha, Kernel kernel, double[][] supportVectors){
        svm_model model = new svm_model();

        model.nr_class = 2;
        model.label = new int[] {1,0};  // {1,-1} do not change!

        model.l = supportVectors.length;
        model.nSV = new int[] {0, model.l};

        model.rho = new double[] {-bias};
        model.sv_coef = new double[][] {alpha};
        model.SV = SvmNodeConverter.toSvmNodeArray(supportVectors);

        model.param = new SvmParameterAdapter().kernel(kernel).build();

        return model;
    }

    /**
     * Converts a Tuple collection into a svm_problem object.
     * @param data: labeled data collection
     * @return svm_problem instance
     */
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
