package machinelearning.classifier;

import data.LabeledDataset;
import explore.user.UserLabel;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a learner for categorical features. It assumes that the input matrix is the one-hot encoding of
 * a categorical feature, and simply memorizes which labels are positive or negative.
 */
public class CategoricalLearner implements Learner {
    @Override
    public Classifier fit(LabeledDataset labeledPoints) {
        Matrix data = labeledPoints.getData();
        UserLabel[] labels = labeledPoints.getLabels();

        Set<Integer> positive = new HashSet<>();
        Set<Integer> negative = new HashSet<>();

        for (int i = 0; i < labels.length; i++) {
            int index = findCategoryIndex(data.getRow(i));
            if (labels[i].isPositive()) {
                positive.add(index);
            }
            else {
                negative.add(index);
            }
        }

        return new CategoricalClassifier(positive, negative);
    }

    static int findCategoryIndex(Vector vector) {
        for (int i = 0; i < vector.dim(); i++) {
            if (vector.get(i) > 0)
                return i;
        }
        throw new RuntimeException("All values are negative!");
    }
}
