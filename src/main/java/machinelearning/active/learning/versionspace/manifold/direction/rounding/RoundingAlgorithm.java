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

package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import utils.Validator;

public class RoundingAlgorithm implements DirectionSamplingAlgorithm {
    private final long maxIter;

    public RoundingAlgorithm(long maxIter) {
        Validator.assertPositive(maxIter);
        this.maxIter = maxIter;
    }

    @Override
    public DirectionSampler fit(ConvexBody body) {
        return new EllipsoidSampler(fitEllipsoid(body), body.getManifold());
    }

    private Ellipsoid fitEllipsoid(ConvexBody body) {
        Ellipsoid ellipsoid = body.getContainingEllipsoid();

        for (int i = 0; i < maxIter; i++) {
            if (!body.attemptToReduceEllipsoid(ellipsoid)) {
                return ellipsoid;
            }
        }

        return ellipsoid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoundingAlgorithm that = (RoundingAlgorithm) o;
        return maxIter == that.maxIter;
    }
}
