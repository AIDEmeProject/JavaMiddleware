package classifier;


import data.LabeledData;

public interface Classifier {
    void fit(LabeledData data);
    int[] predict(LabeledData data);
    double[] probability(LabeledData data);
}
