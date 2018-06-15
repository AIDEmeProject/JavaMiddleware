package user;

import data.LabeledDataset;

/**
 * An User represents the "oracle" of Active Learning scenario: an expert or human annotator capable of, given a data point,
 * return whether it is a positive (i.e. 1) or a negative (i.e. 0) label.
 */
public interface User {
    /**
     * Given a dataset and a row, return the label of data[row]
     * @param data: collection of data points
     * @param row: row to label
     * @return label of data[row]
     */
    int getLabel(LabeledDataset data, int row);

    /**
     * Return the labels of a batch of rows
     * @param data: collection of data points
     * @param rows: collection of rows to request labels
     * @return an array containing the labels of each requested row
     */
    default int[] getLabel(LabeledDataset data, int[] rows){
        int[] labels = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            labels[i] = getLabel(data, rows[i]);
        }
        return labels;
    }

    /**
     * Returns the labels for the entire dataset. This is an utility method used when computing accuracy metrics in experiments.
     * @return labels of all data points
     * TODO: should be removed from the general interface once a "real user" class is added
     */
    int[] getAllLabels(LabeledDataset data);
}
