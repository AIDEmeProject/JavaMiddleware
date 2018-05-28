import data.LabeledData;
import learner.Learner;

import java.util.LinkedHashSet;
import java.util.Random;

public class Explore {
    private static int initialSampling(int[] labels){

        int index = -1;
        int count = 0;

        Random rand = new Random();

        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == 0){
                continue;
            }

            count++;

            if (index < 0 || rand.nextDouble() < 1.0 / count){
                index = i;
            }
        }

        if (index <= 0){
            throw new RuntimeException("Labels are all negative!");
        }

        return index;
    }

    public static LinkedHashSet<Integer> run(double[][] X, int[] y, Learner learner, int budget){
        if (budget <= 0){
            throw new IllegalArgumentException("Budget must be a positive number.");
        }

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        LabeledData labeledData = new LabeledData(X, y);

        // initial sampling
        int row = initialSampling(y);
        labeledData.addLabeledRow(row);

        // fit model to initial sample
        learner.fit(labeledData);

        for (int iter = 0; iter < budget && labeledData.getNumUnlabeledRows() > 0 ; iter++){
            // find next point to label
            row = learner.getNext(labeledData);
            labeledData.addLabeledRow(row);

            // retrain model
            learner.fit(labeledData);
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return labeledData.getLabeledRows();
    }
}
