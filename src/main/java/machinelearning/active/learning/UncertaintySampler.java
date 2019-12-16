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

import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.ProbabilityRanker;
import machinelearning.classifier.Learner;

/**
 * Uncertainty Sampling is the most common Active Learning technique. Basically, it ranks points through the predicted class
 * probabilities: the closed to 0.5, the most uncertain the current model is about the data point, thus knowing its label
 * should bring a high amount of information.
 *
 * @author luciano
 */
public class UncertaintySampler implements ActiveLearner {
    /**
     * internal classifier used for predicting probabilities
     */
    private Learner learner;

    public UncertaintySampler(Learner learner) {
        this.learner = learner;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        return new ProbabilityRanker(learner.fit(labeledPoints));
    }


    public Learner getLearner() {
        return learner;
    }
}
