package machinelearning.classifier.margin;

import data.DataPoint;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;

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
     * @param x: a data point
     * @return the margin of this point
     */
    public abstract double margin(double[] x);

    /**
     * @param point: a data point
     * @return the margin of this point
     */
    public final double margin(DataPoint point){
        return margin(point.getData());
    }

    /**
     * @param point: a data point
     * @return sigmoid( margin(point) )
     */
    @Override
    public final double probability(DataPoint point) {
        return 1.0 / (1.0 + Math.exp(-margin(point)));
    }

    /**
     * @param point: a data point
     * @return sign( margin(x) )
     */
    @Override
    public final Label predict(DataPoint point) {
        return margin(point) > 0 ? Label.POSITIVE : Label.NEGATIVE;
    }
}
