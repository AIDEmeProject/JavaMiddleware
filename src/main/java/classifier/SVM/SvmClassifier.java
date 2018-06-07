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
    private boolean hasProbability;
    private svm_model model;

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
        this.hasProbability = false;
    }

    SvmClassifier(svm_model model) {
        this.model = model;
        this.hasProbability = svm.svm_check_probability_model(model) == 1;
    }

    public double probability(double[] point) {
        if (!hasProbability){
            throw new RuntimeException("Attempting to compute estimate probability, but probability flag is false!");

        }

        double[] probas = new double[2];
        svm.svm_predict_probability(model, SvmNodeConverter.toSvmNodeArray(point), probas);
        return probas[0];
    }

    @Override
    public double probability(LabeledData data, int row) {
        return probability(data.getRow(row));
    }

    public int predict(double[] point) {
        return margin(point) > 0 ? 1 : 0;
    }

    @Override
    public int predict(LabeledData data, int row) {
        return predict(data.getRow(row));
    }

    /**
     * @param point: data point
     * @return sample's signed distance to svm's decision boundary
     */
    public double margin(double[] point){
        double[] margin = new double[1];
        svm.svm_predict_values(model, SvmNodeConverter.toSvmNodeArray(point), margin);
        return margin[0];
    }

    /**
     * @param data: data point
     * @param row : row index
     * @return sample's signed distance to svm's decision boundary
     */
    public double margin(LabeledData data, int row){
        return margin(data.getRow(row));
    }

    private svm_model buildSvmModel(double bias, double[] alpha, Kernel kernel, double[][] supportVectors){
        svm_model model = new svm_model();

        model.nr_class = 2;
        model.label = new int[] {1,0};

        model.l = supportVectors.length;
        model.nSV = new int[] {0, model.l};

        model.rho = new double[] {-bias};
        model.sv_coef = new double[][] {alpha};
        model.SV = SvmNodeConverter.toSvmNodeArray(supportVectors);

        model.param = new SvmParameterAdapter().kernel(kernel).build();

        return model;
    }
}
