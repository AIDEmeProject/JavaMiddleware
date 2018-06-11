package user;

import data.LabeledData;

public interface User {
    int getLabel(LabeledData data, int row);
    int[] getY();
}
