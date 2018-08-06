package classifier;

import data.DataPoint;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a Majority Vote classifier. Given a set of classifiers {H_i}, the majority vote outputs:
 *
 *          P(MV(x) = 1) = (1 / N) * \sum_{i=1}^N I(H_i(x) = 1)
 *
 * In other words, the probability of each class is simply the proportion of classifiers predicting this class.
 */
public class MajorityVoteClassifier implements Classifier {
    /**
     * collection of classifiers to take the majority vote
     */
    private Collection<Classifier> classifiers;

    /**
     * Initialize classifier with an empty collection
     */
    public MajorityVoteClassifier() {
        classifiers = new ArrayList<>();
    }

    /**
     * Add a new classifier to the majority vote
     * @param classifier: new classifier
     * @throws NullPointerException if classifier is null
     */
    public void add(Classifier classifier){
        Validator.assertNotNull(classifier);
        classifiers.add(classifier);
    }

    /**
     * Add all classifiers in collection
     * @param classifiers: collection of classifiers to add
     * @throws NullPointerException if any classifier in the collection is null
     */
    public void addAll(Collection<? extends Classifier> classifiers){
        for (Classifier classifier : classifiers){
            add(classifier);
        }
    }

    /**
     * @param point: data point
     * @return proportion of classifiers predicting the given point as positive
     */
    @Override
    public double probability(DataPoint point) {
        double proba = 0;
        for (Classifier clf : classifiers) {
            proba += clf.predict(point);
        }
        return proba / classifiers.size();
    }
}
