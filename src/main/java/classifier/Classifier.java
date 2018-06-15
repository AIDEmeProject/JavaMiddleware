package classifier;


import data.DataPoint;
import data.LabeledDataset;

/**
 * A classifier is any object capable of "learning from training data" and "make predictions for new data points".
 *
 * @author luciano
 */
public interface Classifier {

    /**
     * Return class probability estimation for a particular point in the dataset.
     * @param data: collection of data points
     * @param row: row index of point of interest
     * @return probability of given point being positive
     */
    double probability(LabeledDataset data, int row);

    double probability(DataPoint point);

    /**
     * Return class probability estimation for each point in the dataset.
     * @param data: collection of data points
     * @return probability estimation array
     */
    default double[] probability(LabeledDataset data){
        double[] probas = new double[data.getNumRows()];

        for (int i = 0; i < data.getNumRows(); i++) {
            probas[i] = probability(data, i);
        }

        return probas;
    }

    /**
     * Return the predicted label for a particular point in the dataset.
     * @param data: collection of data points
     * @param row: row index of point of interest
     * @return class label of given point
     */
    default int predict(LabeledDataset data, int row){
        return probability(data, row) > 0.5 ? 1 : 0;
    }

    /**
     * Return predicted class labels for each point in the dataset.
     * @param data: collection of data points
     * @return predicted class labels
     */
    default int[] predict(LabeledDataset data){
        int[] labels = new int[data.getNumRows()];

        for (int i = 0; i < data.getNumRows(); i++) {
            labels[i] = predict(data, i);
        }

        return labels;
    }
}
