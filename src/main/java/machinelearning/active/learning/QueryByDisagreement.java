/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
