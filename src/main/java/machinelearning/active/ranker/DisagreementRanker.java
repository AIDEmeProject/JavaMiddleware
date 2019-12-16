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
