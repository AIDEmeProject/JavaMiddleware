package user;

import data.LabeledData;

import java.util.Collection;
import java.util.Set;

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
     * @throws IllegalArgumentException if array is empty, contains any number different from 0 or 1, or all labels are identical
     */
    public DummyUser(int[] labels) {
        this.labels = labels;
        validateLabels();
    }

    /**
     * @param indexes: collection of all data point's indexes
     * @param positiveKeys: set of data point's indexes in the target set
     * @throws IllegalArgumentException if array is empty, contains any number different from 0 or 1, or all labels are identical
     */
    public DummyUser(Collection<Long> indexes, Set<Long> positiveKeys) {
        labels = new int[indexes.size()];

        int i = 0;
        for (Long key : indexes) {
            labels[i++] = positiveKeys.contains(key) ? 1 : 0;
        }

        validateLabels();
    }

    private void validateLabels(){
        if (labels.length == 0){
            throw new IllegalArgumentException("Labels array is empty.");
        }

        long sum = 0;
        for (int label : labels) {
            if (label < 0 || label > 1) {
                throw new IllegalArgumentException("Labels must be either 0 or 1.");
            }
            sum += label;
        }

        if (sum == 0 || sum == labels.length){
            throw new IllegalArgumentException("All labels are identical.");
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
