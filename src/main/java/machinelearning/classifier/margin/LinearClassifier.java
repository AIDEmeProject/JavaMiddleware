package machinelearning.classifier.margin;

import utils.Validator;
import utils.linalg.LinearAlgebra;

import java.util.Arrays;

/**
 * A linear classifier. It is defined by two parameters: bias and weight. Predictions are made as in Logistic Regression:
 *
 *      P(y = 1 | x) = sigmoid(bias + weight^T x)
 */
public class LinearClassifier extends MarginClassifier {
    /**
     * Bias parameter
     */
    private final double bias;

    /**
     * Weight vector
     */
    private final double[] weights;

    /**
     * @param bias: bias parameters
     * @param weights: weight vector
     * @throws IllegalArgumentException if weights are empty
     */
    public LinearClassifier(double bias, double[] weights) {
        Validator.assertNotEmpty(weights);

        this.bias = bias;
        this.weights = weights;  //TODO: copy weights array to avoid unintended changes?
    }

    /**
     * Constructs the LinearClassifier from a single weight vector. The first element of this vector may contain the bias,
     * as determined by the hasBias parameter.
     * @param weights: collection of all weights, with the bias possibly being the first element
     * @param hasBias: whether treat weights[0] as the bias
     * @throws IllegalArgumentException if weights is an empty array, or if it contains a single element while hasBias is true
     */
    public LinearClassifier(double[] weights, boolean hasBias) {
        if (weights.length <= (hasBias ? 1 : 0)){
            throw new IllegalArgumentException("Weights array too small: expected at least " + (hasBias ? 1 : 0) + ", but received " + Arrays.toString(weights));
        }
        if (hasBias){
            this.bias = weights[0];
            this.weights = new double[weights.length-1];
            System.arraycopy(weights, 1, this.weights, 0, this.weights.length);
        }
        else {
            this.bias = 0;
            this.weights = weights;
        }
    }

    /**
     * @return dimension of hyperplane
     */
    public int getDim() { return weights.length; }

    /**
     * Compute bias + weight^T x
     */
    public double margin(double[] x){
        return bias + LinearAlgebra.dot(x, weights);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearClassifier that = (LinearClassifier) o;
        return Double.compare(that.bias, bias) == 0 && Arrays.equals(weights, that.weights);
    }
}
