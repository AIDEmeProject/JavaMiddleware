package user;

import data.LabeledData;

/**
 * The DummyUser is a special kind of annotator. It knows the labels of all data points in advance, returning them when
 * prompted. It is useful when developing new algorithms and making benchmarks over known datasets.
 */
public class DummyUser implements User {
    /**
     * true labels
     */
    private int[] labels;

    /**
     * @param labels true labels array. Should only contain 0 or 1 values.
     * @throws IllegalArgumentException if array is empty or contains any number different from 0 or 1
     */
    public DummyUser(int[] labels) {
        if (labels.length == 0){
            throw new IllegalArgumentException("Labels array is empty.");
        }
        validateLabels(labels);
        this.labels = labels;
    }

    private static void validateLabels(int[] y){
        for (int label : y) {
            if (label < 0 || label > 1) {
                throw new IllegalArgumentException("Labels must be either 0 or 1.");
            }
        }
    }

    /**
     * Simply returns labels[row]
     */
    @Override
    public int getLabel(LabeledData data, int row) {
        return labels[row];
    }

    /**
     * Simply returns the internal labels array
     */
    @Override
    public int[] getAllLabels(LabeledData data) {
        return labels;
    }
}
