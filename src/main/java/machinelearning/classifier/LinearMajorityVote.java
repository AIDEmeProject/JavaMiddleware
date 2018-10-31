package machinelearning.classifier;

import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class LinearMajorityVote implements Classifier {
    private Vector bias;
    private Matrix weights;

    public LinearMajorityVote(Vector bias, Matrix weights) {
        Validator.assertEquals(bias.dim(), weights.rows());
        this.bias = bias;
        this.weights = weights;
    }

    public LinearMajorityVote(LinearClassifier[] linearClassifiers) {
        double[] bias = new double[linearClassifiers.length];
        double[][] weights = new double[linearClassifiers.length][];

        for (int i = 0; i < linearClassifiers.length; i++) {
            bias[i] = linearClassifiers[i].getBias();
            weights[i] = linearClassifiers[i].getWeights().toArray();
        }

        this.bias = Vector.FACTORY.make(bias);
        this.weights = Matrix.FACTORY.make(weights);
    }

    public int getDim() {
        return weights.cols();
    }

    @Override
    public Label predict(Vector vector) {
        return margin(vector).iApplyMap(x -> x > 0 ? 1D : -1D).sum() > 0 ? Label.POSITIVE : Label.NEGATIVE;
    }

    @Override
    public Label[] predict(Matrix matrix) {
        Vector sums = margin(matrix).iApplyMap(x -> x > 0 ? 1D : -1D).getRowSums();

        Label[] labels = new Label[sums.dim()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = sums.get(i) > 0 ? Label.POSITIVE : Label.NEGATIVE;
        }

        return labels;
    }

    @Override
    public double probability(Vector vector) {
        return margin(vector).iApplyMap(x -> x > 0 ? 1D : 0D).sum() / bias.dim();
    }

    @Override
    public Vector probability(Matrix matrix) {
        return margin(matrix).iApplyMap(x -> x > 0 ? 1D : 0D).getRowSums().iScalarDivide(bias.dim());
    }

    private Vector margin(Vector vector) {
        return weights.multiply(vector).iAdd(bias);
    }

    private Matrix margin(Matrix matrix) {
        return matrix.multiplyTranspose(weights).iAddRow(bias);
    }
}
