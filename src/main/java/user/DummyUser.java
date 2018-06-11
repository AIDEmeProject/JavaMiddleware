package user;

import data.LabeledData;

public class DummyUser implements User {
    private int[] labels;

    public DummyUser(int[] labels) {
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

    @Override
    public int getLabel(LabeledData data, int row) {
        return labels[row];
    }

    @Override
    public int[] getY() {
        return labels;
    }
}
