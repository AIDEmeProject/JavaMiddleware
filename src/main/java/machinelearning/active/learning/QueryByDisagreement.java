package machinelearning.active.learning;

import data.IndexedDataset;
import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.DisagreementRanker;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import utils.Validator;

import java.util.Arrays;

public class QueryByDisagreement implements ActiveLearner {

    private final Learner learner;
    private final int backgroundSampleSize;
    private IndexedDataset dataset;

    public QueryByDisagreement(Learner learner, int backgroundSampleSize) {
        Validator.assertPositive(backgroundSampleSize);

        this.learner = learner;
        this.backgroundSampleSize = backgroundSampleSize;
    }

    public void setDataset(IndexedDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        IndexedDataset backgroundPoints = dataset.sample(backgroundSampleSize);
        Label[] fakeLabels = new Label[backgroundSampleSize];

        //TODO: how to weight data?
        Arrays.fill(fakeLabels, Label.POSITIVE);
        LabeledDataset positivelyBiasedDataset = labeledPoints.append(backgroundPoints, fakeLabels);
        Classifier positivelyBiasedClassifier = learner.fit(positivelyBiasedDataset);

        Arrays.fill(fakeLabels, Label.NEGATIVE);
        LabeledDataset negativelyBiasedDataset = labeledPoints.append(backgroundPoints, fakeLabels);
        Classifier negativelyBiasedClassifier = learner.fit(negativelyBiasedDataset);

        return new DisagreementRanker(positivelyBiasedClassifier, negativelyBiasedClassifier);
    }
}
