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
    public double[] predictProba(LabeledData data) {
        return classifier.probability(data);
    }

    @Override
    public double rank(double[] point) {
        return 0; //Math.abs(classifier.probability(point) - 0.5) ;
    }
}
