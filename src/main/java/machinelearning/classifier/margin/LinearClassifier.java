package machinelearning.classifier.margin;

import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * A linear classifier. It is defined by two parameters: bias and weight. Predictions are made as in Logistic Regression:
 *
 *      P(y = 1 | x) = sigmoid(bias + weight^T x)
 */
public class LinearClassifier extends MarginClassifier {

    private final HyperPlane hyperplane;

    /**
     * @param bias: bias parameters
     * @param weights: weight vector
     * @throws IllegalArgumentException if weights are empty
     */
    public LinearClassifier(double bias, Vector weights) {
        this.hyperplane = new HyperPlane(bias, weights);
    }

    /**
     * @return dimension of hyperplane
     */
    public int dim() { return hyperplane.dim(); }

    /**
     * Compute bias + weight^T x
     */
    @Override
    public double margin(Vector x){
        return hyperplane.margin(x);
    }

    @Override
    public Vector margin(Matrix xs) {
        return hyperplane.margin(xs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearClassifier that = (LinearClassifier) o;
        return Objects.equals(hyperplane, that.hyperplane);
    }
}
