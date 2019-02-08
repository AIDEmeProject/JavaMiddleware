package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import utils.linalg.Vector;

public class DisagreementRanker implements Ranker {
    private final Classifier positiveClassifier;
    private final Classifier negativeClassifier;

    public DisagreementRanker(Classifier positiveClassifier, Classifier negativeClassifier) {
        this.positiveClassifier = positiveClassifier;
        this.negativeClassifier = negativeClassifier;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        Label[] positiveClassifierLabels = positiveClassifier.predict(unlabeledData);
        Label[] negativeClassifierLabels = negativeClassifier.predict(unlabeledData);

        Vector score = Vector.FACTORY.zeros(unlabeledData.length());
        for (int i = 0; i < score.dim(); i++) {
            if (positiveClassifierLabels[i] != negativeClassifierLabels[i]) {
                score.set(i, -1);
                break;
            }
        }

        return score;
    }
}
