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
import utils.linalg.Vector;

import java.util.Arrays;

public class QueryByDisagreement implements ActiveLearner {

    private final Learner learner;
    private final int backgroundSampleSize;
    private final double backgroundSamplesWeight;
    private IndexedDataset dataset;

    public QueryByDisagreement(Learner learner, int backgroundSampleSize, double backgroundSamplesWeight) {
        Validator.assertPositive(backgroundSampleSize);
        Validator.assertPositive(backgroundSamplesWeight);

        this.learner = learner;
        this.backgroundSampleSize = backgroundSampleSize;
        this.backgroundSamplesWeight = backgroundSamplesWeight;
    }

    public void setDataset(IndexedDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        IndexedDataset backgroundPoints = dataset.sample(backgroundSampleSize);
        Label[] fakeLabels = new Label[backgroundSampleSize];

        Vector sampleWeights = Vector.FACTORY.fill(labeledPoints.length() + backgroundSampleSize, 1.0);
        for (int i = labeledPoints.length(); i < sampleWeights.dim(); i++) {
            sampleWeights.set(i, backgroundSamplesWeight);
        }

        Arrays.fill(fakeLabels, Label.POSITIVE);
        LabeledDataset positivelyBiasedDataset = labeledPoints.append(backgroundPoints, fakeLabels);
        Classifier positivelyBiasedClassifier = learner.fit(positivelyBiasedDataset, sampleWeights);

        Arrays.fill(fakeLabels, Label.NEGATIVE);
        LabeledDataset negativelyBiasedDataset = labeledPoints.append(backgroundPoints, fakeLabels);
        Classifier negativelyBiasedClassifier = learner.fit(negativelyBiasedDataset, sampleWeights);

        return new DisagreementRanker(positivelyBiasedClassifier, negativelyBiasedClassifier);
    }
}
