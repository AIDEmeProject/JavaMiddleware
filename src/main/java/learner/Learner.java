package learner;

import data.LabeledData;

public interface Learner {
    void fit(LabeledData data);

    double rank(LabeledData data, int rowNumber);

    default int getNext(LabeledData data){
        double minScore = Double.POSITIVE_INFINITY;
        int pos = -1;

        // TODO: maybe its better to create a single iterator over unlabeled points (index, x[index], y[index]) ?

        for(int i=0; i < data.getNumRows(); i++){
            if(data.rowIsLabeled(i)){
                continue;
            }

            double score = rank(data, i);
            if(score < minScore){
                minScore = score;
                pos = i;
            }
        }

        return pos;
    }
}
