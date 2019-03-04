package machinelearning.classifier;

import utils.linalg.Vector;

import java.util.Set;

public class CategoricalClassifier implements Classifier {
    private final Set<Integer> positive;
    private final Set<Integer> negative;

    public CategoricalClassifier(Set<Integer> positive, Set<Integer> negative) {
        this.positive = positive;
        this.negative = negative;
    }

    @Override
    public double probability(Vector vector) {
        int index = CategoricalLearner.findCategoryIndex(vector);

        if (positive.contains(index)) {
            return 1.0;
        }
        else if (negative.contains(index)) {
            return 0.0;
        }

        return 0.5;
    }
}
