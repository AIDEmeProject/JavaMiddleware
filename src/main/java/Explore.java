import data.LabeledData;
import learner.Learner;
import sampling.ReservoirSampler;

import java.util.Collection;

public class Explore {
    private static int sampleLabel(int[] labels, int label){
        // sample an index between 0 and labels.length - 1, excluding points of opposite label
        return ReservoirSampler.sample(labels.length, i -> labels[i] == 1 - label);
    }

    public static Collection<Integer> run(double[][] X, int[] y, Learner learner, int budget){
        if (budget <= 0){
            throw new IllegalArgumentException("Budget must be a positive number.");
        }

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        LabeledData labeledData = new LabeledData(X, y);

        // initial sampling: one negative and one positive random samples
        int row = sampleLabel(y, 0);
        labeledData.addLabeledRow(row);

        row = sampleLabel(y, 1);
        labeledData.addLabeledRow(row);

        // fit model to initial sample
        learner.fit(labeledData);

        for (int iter = 0; iter < budget && labeledData.getNumUnlabeledRows() > 0; iter++){
            // find next point to label
            row = learner.retrieveMostInformativeUnlabeledPoint(labeledData);
            labeledData.addLabeledRow(row);

            // retrain model
            learner.fit(labeledData);
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return labeledData.getLabeledRows();
    }
}
