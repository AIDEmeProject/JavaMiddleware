package learner;

import classifier.Classifier;
import data.LabeledData;

public class UncertaintySampler implements Learner {

    private Classifier classifier;

    public UncertaintySampler(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void fit(LabeledData data) {
        classifier.fit(data);
    }

    @Override
    public double rank(LabeledData data, int rowNumber) {
        return Math.abs(classifier.probability(data, rowNumber) - 0.5) ;
    }
}
