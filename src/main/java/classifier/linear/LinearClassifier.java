package classifier.linear;

import classifier.Classifier;
import data.DataPoint;
import utils.Validator;
import utils.linalg.LinearAlgebra;

/**
 * A linear classifier. It is defined by two parameters: bias and weight. Predictions are made as in Logistic Regression:
 *
 *      P(y = 1 | x) = sigmoid(bias + weight^T x)
 */
public class LinearClassifier implements Classifier {
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
        this.weights = weights;
    }

    /**
     * Constructs the LinearClassifier from a single weight vector. The first element of this vector may contain the bias,
     * as determined by the hasBias parameter.
     * @param weights: collection of all weights, with the bias possibly being the first element
     * @param hasBias: whether treat weights[0] as the bias
     */
    public LinearClassifier(double[] weights, boolean hasBias) {
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

    public double getBias() {
        return bias;
    }

    public double[] getWeights() {
        //TODO: return copy so to avoid unintended changes?
        return weights;
    }

    /**
     * Computes bias + weight^T x
     */
    private double margin(double[] x){
        return bias + LinearAlgebra.dot(x, weights);
    }

    @Override
    public double probability(DataPoint point) {
        return 1. / (1. + Math.exp(-margin(point.getData())));
    }

    @Override
    public int predict(DataPoint point) {
        return margin(point.getData()) > 0 ? 1 : 0;
    }
}
