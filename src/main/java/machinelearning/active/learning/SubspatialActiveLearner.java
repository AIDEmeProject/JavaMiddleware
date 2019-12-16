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
import machinelearning.active.ranker.SubspatialRanker;
import machinelearning.active.ranker.subspatial.LossFunction;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import machinelearning.classifier.SubspatialLearner;

/**
 * A Subspatial Active Learner decomposes the learning task across each feature subspace. Basically, one particular
 * Learner is fit over each data subspace using the partial label information, and a final {@link Ranker} object returned
 * which pieces together all fitted {@link Classifier} objects.
 */
public class SubspatialActiveLearner implements ActiveLearner {

    /**
     * Active Learners to be fit to each subspace
     */
    private final SubspatialLearner subspatialLearner;

    /**
     * Function connecting the subspace probabilities into a final informativeness score
     */
    private final LossFunction lossFunction;

    public SubspatialActiveLearner(SubspatialLearner subspatialLearner, LossFunction lossFunction) {
        this.subspatialLearner = subspatialLearner;
        this.lossFunction = lossFunction;
    }

    public Learner getSubspatialLearner() {
        return subspatialLearner;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        return new SubspatialRanker(subspatialLearner.fit(labeledPoints), lossFunction);
    }
}
