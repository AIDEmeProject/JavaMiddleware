package machinelearning.classifier.margin;

import data.DataPoint;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * A margin classifier is defined by:
 *
 *      \( h(x) = sign( T(x) ) \)
 *
 * where T(x) is a "margin" function. In other words, T(x) returns the signed distance of "x" to the decision boundary
 * (which is given by \( \{x : T(x) = 0\} \).
 *
 * Probability calculations are made through application of the sigmoid function to the margin.
 */
public abstract class MarginClassifier implements Classifier {
    /**
     * @param x: a feature vector
     * @return the margin of this point
     */
    public abstract double margin(Vector x);

    /**
     * @param xs: a matrix of feature vectors (one per row)
     * @return a Vector containing the margins of each feature vector
     */
    public abstract Vector margin(Matrix xs);

    /**
     * @param point: a data point
     * @return the margin of this point
     */
    public final double margin(DataPoint point){
        return margin(point.getData());
    }

    @Override
    public double probability(Vector vector) {
        return sigmoid(margin(vector));
    }

    @Override
    public Vector probability(Matrix matrix) {
        return margin(matrix).iApplyMap(MarginClassifier::sigmoid);
    }

    private static double sigmoid(double value) {
        return 1.0 / (1.0 + Math.exp(-value));
    }

    /**
     * @param point: a data point
     * @return sign( margin(x) )
     */
    @Override
    public final Label predict(Vector point) {
        return Label.fromSign(margin(point));
    }
}
