package machinelearning.classifier;


import data.DataPoint;
import data.LabeledDataset;

import java.util.Collection;

/**
 * A classifier is any object capable of "learning from training data" and "make predictions for new data points".
 *
 * @author luciano
 */
public interface Classifier {

    /**
     * Return class probability estimation for a particular point in the dataset.
     * @param point: data point
     * @return probability of point being positive
     */
    double probability(DataPoint point);

    /**
     * Return class probability estimation for each point in the dataset.
     * @param points: collection of data points
     * @return probability estimation array
     */
    default double[] probability(Collection<DataPoint> points){
        double[] probas = new double[points.size()];

        int i = 0;
        for (DataPoint point : points) {
            probas[i++] = probability(point);
        }

        return probas;
    }

    /**
     * Return the predicted label for a particular point in the dataset.
     * @param point: data point
     * @return class label of given point
     */
    default Label predict(DataPoint point){
        return probability(point) > 0.5 ? Label.POSITIVE : Label.NEGATIVE;
    }

    /**
     * Return predicted class labels for each point in the dataset.
     * @param points: collection of data points
     * @return predicted class labels
     */
    default Label[] predict(Collection<DataPoint> points){
        Label[] labels = new Label[points.size()];

        int i = 0;
        for (DataPoint point : points) {
            labels[i++] = predict(point);
        }

        return labels;
    }

    /**
     * Upper bound on "future probabilities".
     *
     * @param data: labeled data
     * @param maxPositivePoints: maximum number of positive labeled points
     * @return probability upper bound
     */
    default double computeProbabilityUpperBound(Collection<DataPoint> data, int maxPositivePoints){
        return Double.POSITIVE_INFINITY;
    }
}
