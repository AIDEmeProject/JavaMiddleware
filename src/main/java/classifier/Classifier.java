package classifier;


import data.LabeledData;

/**
 * A classifier is any object capable of "learning from training data" and "make predictions for new data points".
 *
 * @author luciano
 */
public interface Classifier {
    /**
     * Train its classification model over training data. Only the labeled points should be considered for training.
     * @param data: collection of labeled points
     * TODO: throw exception if labeled set is empty?
     */
    void fit(LabeledData data);

    /**
     * Return class probability estimation for a particular point in the dataset.
     * @param data: collection of data points
     * @param row: row index of point of interest
     * @return probability of given point being positive
     */
    double probability(LabeledData data, int row);

    /**
     * Return class probability estimation for each point in the dataset.
     * @param data: collection of data points
     * @return probability estimation array
     */
    default double[] probability(LabeledData data){
        double[] probas = new double[data.getNumRows()];

        for (int i = 0; i < data.getNumRows(); i++) {
            probas[i] = probability(data, i);
        }

        return probas;
    }

}
