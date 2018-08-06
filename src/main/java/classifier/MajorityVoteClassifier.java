package classifier;

import data.DataPoint;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

public class MajorityVoteClassifier implements Classifier {
    private Collection<Classifier> classifiers;

    public MajorityVoteClassifier() {
        classifiers = new ArrayList<>();
    }

    public void add(Classifier classifier){
        Validator.assertNotNull(classifier);
        classifiers.add(classifier);
    }

    public void addAll(Collection<? extends Classifier> classifiers){
        for (Classifier classifier : classifiers){
            add(classifier);
        }
    }

    @Override
    public double probability(DataPoint point) {
        double proba = 0;
        for (Classifier clf : classifiers) {
            proba += clf.predict(point);
        }
        return proba / classifiers.size();
    }
}
