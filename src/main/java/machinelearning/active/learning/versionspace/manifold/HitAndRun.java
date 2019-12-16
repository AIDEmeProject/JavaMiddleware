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

package machinelearning.active.learning.versionspace.manifold;

import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import utils.RandomState;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;
import java.util.Random;

public final class HitAndRun {

    private final ConvexBody body;
    private final DirectionSampler sampler;

    public HitAndRun(ConvexBody body, DirectionSampler sampler) {
        this.body = Objects.requireNonNull(body);
        this.sampler = Objects.requireNonNull(sampler);
    }

    public Chain newChain() {
        return new Chain(body, sampler, RandomState.newInstance());
    }

    public final static class Chain {
        private final ConvexBody body;
        private final DirectionSampler sampler;
        private final Random random;
        private Vector currentSample;

        Chain(ConvexBody body, DirectionSampler sampler, Random random) {
            this.body = body;
            this.sampler = sampler;
            this.random = random;
            this.currentSample = body.getInteriorPoint();
        }

        public Vector advance() {
            Vector randomDirection = sampler.sampleDirection(currentSample, random);
            Geodesic geodesic = body.getManifold().getGeodesic(currentSample, randomDirection);
            GeodesicSegment segment = body.computeIntersection(geodesic);
            currentSample = segment.getPoint(random.nextDouble());
            return currentSample;
        }

        public Vector advance(int n) {
            Validator.assertPositive(n);
            for (int i = 0; i < n; i++) {
                advance();
            }
            return currentSample;
        }
    }
}
