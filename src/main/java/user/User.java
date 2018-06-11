package user;

import data.LabeledData;

public interface User {
    int getLabel(LabeledData data, int row);

    default int[] getLabel(LabeledData data, int[] rows){
        int[] labels = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            labels[i] = getLabel(data, rows[i]);
        }
        return labels;
    }

    int[] getY();
}
