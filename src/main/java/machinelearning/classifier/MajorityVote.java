package machinelearning.classifier;

import utils.Validator;
import utils.linalg.Vector;

import java.util.Arrays;

/**
 * This class represents a Majority Vote classifier. Given a set of classifiers {H_i}, the majority vote MV outputs:
 *
 *          P(MV(x) = 1) = (1 / N) * \sum_{i=1}^N I(H_i(x) = 1)
 *
 * In other words, the probability of each class is simply the proportion of classifiers agreeing on this class.
 */
public class MajorityVote<T extends Classifier> implements Classifier {
    /**
     * collection of classifiers to take the majority vote
     */
    private final T[] classifiers;

    /**
     * @param classifiers: array of classifiers used in Majority Vote computation
     * @throws IllegalArgumentException if classifiers array is empty or contains null elements
     */
    public MajorityVote(T[] classifiers) {
        Validator.assertNotEmpty(classifiers);

        for (Classifier classifier : classifiers){
            if (classifier == null){
                throw new NullPointerException("Classifier cannot be null.");
            }
        }

        this.classifiers = classifiers;
    }

    public T[] getClassifiers() {
        return classifiers;
    }

    /**
     * @param vector: feature vector
     * @return proportion of classifiers predicting the given point as positive
     */
    @Override
    public double probability(Vector vector) {
        double proba = 0;
        for (Classifier clf : classifiers) {
            proba += clf.predict(vector).asBinary();
        }
        return proba / classifiers.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MajorityVote<?> that = (MajorityVote<?>) o;
        return Arrays.equals(classifiers, that.classifiers);
    }
}
