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

package machinelearning.active.learning.versionspace.manifold.direction;

import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import utils.linalg.Vector;

import java.util.Random;

/**
 * This is an interface for all direction sampling algorithms. Mathematically, we intend to implement algorithms for
 * sampling vectors on the unit sphere, which will be used by the {@link HitAndRunSampler} for sampling from a
 * {@link machinelearning.active.learning.versionspace.manifold.ConvexBody}.
 */
public interface DirectionSampler {
    /**
     * @param rand a random number generator instance
     * @return a random direction. We do NOT guarantee the output will have unit norm.
     */
    Vector sampleDirection(Vector point, Random rand);
}
