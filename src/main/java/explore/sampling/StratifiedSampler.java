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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StratifiedSampler implements InitialSampler{
    /**
     * number of positive samples to be sampled
     */
    private final int positiveSamples;

    /**
     * number of negative samples to be sampled
     */
    private final int negativeSamples;

    private final boolean negativeInAllSubspaces;

    /**
     * NegativeInAllSubspaces defaults to false.
     * @throws IllegalArgumentException if either positiveSamples or negativeSamples are negative
     */
    public StratifiedSampler(int positiveSamples, int negativeSamples) {
        this(positiveSamples, negativeSamples, false);
    }

    /**
     * @throws IllegalArgumentException if either positiveSamples or negativeSamples are negative
     */
    public StratifiedSampler(int positiveSamples, int negativeSamples, boolean negativeInAllSubspaces) {
        Validator.assertPositive(positiveSamples);
        Validator.assertPositive(negativeSamples);

        this.positiveSamples = positiveSamples;
        this.negativeSamples = negativeSamples;
        this.negativeInAllSubspaces = negativeInAllSubspaces;
    }

    /**
     * @param unlabeledSet: initial collection of unlabeled points
     * @param user: user instance for labeling points
     * @return a list of DataPoints containing the specified number of positive and negative samples
     */
    public List<DataPoint> runInitialSample(IndexedDataset unlabeledSet, User user){
        List<DataPoint> samples = new ArrayList<>(positiveSamples + negativeSamples);

        if (positiveSamples > 0){
            List<DataPoint> positivePoints = unlabeledSet.stream()
                    .filter(x -> user.getLabel(x).isPositive())
                    .collect(Collectors.toList());

            if (positivePoints.size() < positiveSamples) {
                throw new RuntimeException("Dataset does not contain " + positivePoints + " positive points.") ;
            }

            samples.addAll(ReservoirSampler.sample(positivePoints, positiveSamples));
        }

        if (negativeSamples > 0){
            Predicate<DataPoint> filter = negativeInAllSubspaces ? x -> user.getLabel(x).isAllNegative() : x -> user.getLabel(x).isNegative();

            List<DataPoint> negativePoints = unlabeledSet.stream()
                    .filter(filter)
                    .collect(Collectors.toList());

            if (negativePoints.size() < negativeSamples) {
                throw new RuntimeException("Dataset does not contain " + negativePoints + " negative points.") ;
            }

            samples.addAll(ReservoirSampler.sample(negativePoints, negativeSamples));
        }

        return samples;
    }

    @Override
    public String toString() {
        return "StratifiedSampler{" +
                "positiveSamples=" + positiveSamples +
                ", negativeSamples=" + negativeSamples +
                '}';
    }
}
