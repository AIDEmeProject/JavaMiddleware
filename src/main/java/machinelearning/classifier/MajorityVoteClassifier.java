package machinelearning.classifier;

import data.DataPoint;
import utils.Validator;

/**
 * This class represents a Majority Vote classifier. Given a set of classifiers {H_i}, the majority vote MV outputs:
 *
 *          P(MV(x) = 1) = (1 / N) * \sum_{i=1}^N I(H_i(x) = 1)
 *
 * In other words, the probability of each class is simply the proportion of classifiers agreeing on this class.
 */
public class MajorityVoteClassifier implements Classifier {
    /**
     * collection of classifiers to take the majority vote
     */
    private final Classifier[] classifiers;

    /**
     * @param classifiers: array of classifiers used in Majority Vote computation
     * @throws IllegalArgumentException if classifiers array is empty or contains null elements
     */
    public MajorityVoteClassifier(Classifier[] classifiers) {
        Validator.assertNotEmpty(classifiers);

        for (Classifier classifier : classifiers){
            if (classifier == null){
                throw new NullPointerException("Classifier cannot be null.");
            }
        }

        this.classifiers = classifiers;
    }

    /**
     * @param point: data point
     * @return proportion of classifiers predicting the given point as positive
     */
    @Override
    public double probability(DataPoint point) {
        double proba = 0;
        for (Classifier clf : classifiers) {
            proba += clf.predict(point).asBinary();
        }
        return proba / classifiers.length;
    }
}
