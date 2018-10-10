package machinelearning.classifier;


import data.DataPoint;
import data.IndexedDataset;
import utils.linalg.Vector;

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
    default Vector probability(IndexedDataset points){
        double[] probas = new double[points.length()];

        int i = 0;
        for (DataPoint point : points) {
            probas[i++] = probability(point);
        }

        return Vector.FACTORY.make(probas);
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
    default Label[] predict(IndexedDataset points){
        Label[] labels = new Label[points.length()];

        int i = 0;
        for (DataPoint point : points) {
            labels[i++] = predict(point);
        }

        return labels;
    }
}
