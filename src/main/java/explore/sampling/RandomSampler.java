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

package explore.sampling;

import data.DataPoint;
import data.IndexedDataset;
import explore.user.User;
import utils.Validator;

import java.util.List;

/**
 * The RandomSampler performs a random selection of initial points of specified size.
 *
 * The underlying algorithm is very simple: if N is the dataset size, and m is the sample size, we repeatedly sample random
 * integers between 0 and N-1 until m distinct values have been retrieved. These m random indexes will be then used to
 * retrieve the data points from the dataset.
 *
 * Note that this algorithm assumes that the sample size is considerably smaller than the underlying dataset; otherwise
 * the performance penalty can be considerably high.
 */
public class RandomSampler implements InitialSampler {
    private final int sampleSize;

    public RandomSampler(int sampleSize) {
        Validator.assertPositive(sampleSize);
        this.sampleSize = sampleSize;
    }

    @Override
    public List<DataPoint> runInitialSample(IndexedDataset unlabeledSet, User user) {
        return unlabeledSet.sample(sampleSize).toList();
    }
}
