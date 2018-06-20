package classifier;


import data.DataPoint;

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
    default int predict(DataPoint point){
        return probability(point) > 0.5 ? 1 : 0;
    }

    /**
     * Return predicted class labels for each point in the dataset.
     * @param points: collection of data points
     * @return predicted class labels
     */
    default int[] predict(Collection<DataPoint> points){
        int[] labels = new int[points.size()];

        int i = 0;
        for (DataPoint point : points) {
            labels[i++] = predict(point);
        }

        return labels;
    }
}
