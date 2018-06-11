package user;

import data.LabeledData;

public class DummyUser implements User {
    private int[] labels;

    public DummyUser(int[] labels) {
        this.labels = labels;
    }

    @Override
    public int getLabel(LabeledData data, int row) {
        return labels[row];
    }
}
