package classifier;


import data.LabeledData;

public interface Classifier {
    void fit(LabeledData data);

    int[] predict(LabeledData data);

    double probability(LabeledData data, int rowNumber);

    default double[] probability(LabeledData data){
        double[] probas = new double[data.getNumRows()];

        for (int i = 0; i < data.getNumRows(); i++) {
            probas[i] = probability(data, i);
        }

        return probas;
    }

}
