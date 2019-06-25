package machinelearning.classifier;

import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * This class represents a Majority Vote classifier. Given a set of classifiers {H_i}, the majority vote MV outputs:
 *
 *          P(MV(x) = 1) = (1 / N) * \sum_{i=1}^N I(H_i(x) = 1)
 *
 * In other words, the probability of each class is simply the proportion of classifiers agreeing on this class.
 */
public class LinearMajorityVote implements Classifier {
    private Vector bias;
    private Matrix weights;

    public LinearMajorityVote(Vector bias, Matrix weights) {
        Validator.assertEquals(bias.dim(), weights.rows());
        this.bias = bias;
        this.weights = weights;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearMajorityVote that = (LinearMajorityVote) o;
        return Objects.equals(bias, that.bias) &&
                Objects.equals(weights, that.weights);
    }
}
