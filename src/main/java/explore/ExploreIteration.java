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

package explore;

import config.ExperimentConfiguration;
import data.DataPoint;
import data.IndexedDataset;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.active.Ranker;
import utils.RandomState;

import java.util.Collections;
import java.util.List;

/**
 * This module encodes a typical iteration of the Active Learning exploration phase, after the Initial Sampling phase.
 * Here, the next points to label are chosen by the following process:
 *
 *   1) First we must select an unlabeled collection of points to search the most informative point from. With a certain probability,
 *   we search the collection of all Unlabeled Points so far; otherwise, we search only the Unknown collection, i.e. the points
 *   for which our data model is still unsure about. In the first case, the AL algorithm converges faster, while in the second
 *   case our data model suffers the most change.
 *
 *   2) Then, the unlabeled selection of points is further SUB-SAMPLED. This is done to reduce the time-per-iteration.
 *
 *   3) Finally, the Active Learning algorithm runs over the above sub-sample, and a most informative point is retrieved.
 */
public class ExploreIteration extends Iteration {
    /**
     * Size of subsample in step 2
     */
    private final int subsampleSize;

    /**
     * Probability of selecting the unknown region for running the AL algorithm.
     */
    private final double searchUnknownRegionProbability;

    public ExploreIteration(ExperimentConfiguration configuration) {
        super(configuration);
        this.subsampleSize = configuration.getSubsampleSize();
        this.searchUnknownRegionProbability = configuration.getTsmConfiguration().getSearchUnknownRegionProbability();
    }

    @Override
    public List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, User user, Ranker ranker) {
        IndexedDataset unlabeledData = getUnlabeledData(partitionedDataset);
        IndexedDataset sample = unlabeledData.sample(subsampleSize);
        return Collections.singletonList(ranker.top(sample));
    }

    private IndexedDataset getUnlabeledData(PartitionedDataset dataset) {
        boolean useUnknown = dataset.hasUnknownPoints() && RandomState.newInstance().nextDouble() <= searchUnknownRegionProbability;
        return useUnknown ? dataset.getUnknownPoints() : dataset.getUnlabeledPoints();
    }
}