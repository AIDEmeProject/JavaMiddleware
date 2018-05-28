package learner;

import data.LabeledData;

public interface Learner {
    void fit(LabeledData data);

    double[] predictProba(LabeledData data);

    double rank(double[] point);

    default int getNext(LabeledData data){
        double minRank = Double.NEGATIVE_INFINITY;
        int pos = -1;

        // TODO: maybe its better to create a single iterator over unlabeled points (index, x[index], y[index]) ?

        for(int i=0; i < data.getNumRows(); i++){
            if(data.checkRowIsLabeled(i)){
                continue;
            }

            double score = rank(data.getX()[i]);
            if(score > minRank){
                minRank = score;
                pos = i;
            }
        }

        return pos;
    }
}
