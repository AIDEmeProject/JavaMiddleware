package machinelearning.classifier.margin;

import utils.linalg.Matrix;
import utils.linalg.Vector;

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
    private final Vector weights;

    /**
     * @param bias: bias parameters
     * @param weights: weight vector
     * @throws IllegalArgumentException if weights are empty
     */
    public LinearClassifier(double bias, Vector weights) {
        this.bias = bias;
        this.weights = weights;
    }

    /**
     * Constructs the LinearClassifier from a single weight vector. The first element of this vector may contain the bias,
     * as determined by the hasBias parameter.
     * @param weights: collection of all weights, with the bias possibly being the first element
     * @param hasBias: whether treat weights[0] as the bias
     * @throws IllegalArgumentException if weights is an empty array, or if it contains a single element while hasBias is true
     */
    public LinearClassifier(Vector weights, boolean hasBias) {
        if (weights.dim() <= (hasBias ? 1 : 0)){
            throw new IllegalArgumentException("Weights array too small: expected at least " + (hasBias ? 1 : 0) + ", but received " + weights);
        }
        if (hasBias){
            this.bias = weights.get(0);
            this.weights = weights.slice(1, weights.dim());
        }
        else {
            this.bias = 0;
            this.weights = weights;
        }
    }

    /**
     * @return dimension of hyperplane
     */
    public int getDim() { return weights.dim(); }

    /**
     * Compute bias + weight^T x
     */
    @Override
    public double margin(Vector x){
        return bias + x.dot(weights);
    }

    @Override
    public Vector margin(Matrix xs) {
        return xs.multiply(weights).scalarAdd(bias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearClassifier that = (LinearClassifier) o;
        return Double.compare(that.bias, bias) == 0 && weights.equals(that.weights);
    }
}
