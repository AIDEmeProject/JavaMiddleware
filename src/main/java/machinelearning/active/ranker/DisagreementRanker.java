package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import utils.RandomState;
import utils.linalg.Vector;

import java.util.stream.IntStream;

public class DisagreementRanker implements Ranker {
    private final Classifier positiveClassifier;
    private final Classifier negativeClassifier;

    public DisagreementRanker(Classifier positiveClassifier, Classifier negativeClassifier) {
        this.positiveClassifier = positiveClassifier;
        this.negativeClassifier = negativeClassifier;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        // compute positively and negatively biased predictions
        Label[] positiveClassifierLabels = positiveClassifier.predict(unlabeledData);
        Label[] negativeClassifierLabels = negativeClassifier.predict(unlabeledData);

        // select a random row such that the predictions differ
        int[] rows = IntStream
                .range(0, unlabeledData.length())
                .filter(row -> positiveClassifierLabels[row] != negativeClassifierLabels[row])
                .toArray();

        if (rows.length == 0) {
            System.out.println("Falling back to RANDOM sampling -------------");
            return new RandomRanker().score(unlabeledData);
        }

        int randomRow = rows[RandomState.newInstance().nextInt(rows.length)];

        // return score function: all zeros, except for the randomly selected differing point
        Vector score = Vector.FACTORY.zeros(unlabeledData.length());
        score.set(randomRow, -1);
        return score;
    }
}
