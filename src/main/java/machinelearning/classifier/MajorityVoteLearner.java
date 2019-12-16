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

package machinelearning.classifier;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.VersionSpace;
import utils.Validator;

/**
 * This module builds a majority vote classifier by sampling from the {@link VersionSpace}.
 */
public class MajorityVoteLearner implements Learner {
    /**
     * Number of classifiers to sample from the version space
     */
    private final int sampleSize;

    /**
     * {@link VersionSpace} of classifiers
     */
    private final VersionSpace versionSpace;

    /**
     * @param versionSpace: version space instance
     * @param sampleSize: number of samples used to build a majority vote classifier
     * @throws IllegalArgumentException if versionSpace is null or sampleSize is not positive
     */
    public MajorityVoteLearner(VersionSpace versionSpace, int sampleSize) {
        Validator.assertNotNull(versionSpace);
        Validator.assertPositive(sampleSize);

        this.versionSpace = versionSpace;
        this.sampleSize = sampleSize;
    }

    /**
     * @param labeledPoints: collection of labeled points
     * @return Majority vote classifier constructed by sampling from the Version Space delimited by the labeledPoints.
     */
    @Override
    public Classifier fit(LabeledDataset labeledPoints) {
        return versionSpace.sample(labeledPoints, sampleSize);
    }
}
