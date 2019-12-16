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


import machinelearning.active.learning.versionspace.manifold.cache.ConvexBodyCache;
import machinelearning.active.learning.versionspace.manifold.cache.EllipsoidCache;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.EllipsoidSampler;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.RoundingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * This class is responsible for configuring a sampling strategy for the Hit-and-Run algorithm. We can configure 4 sampling
 * parameters:
 *
 *      1) {@link SampleSelector}: how to select "n" samples from the Hit-and-Run chain
 *      2) {@link SampleCache}: caching strategy for cases where similar objects are repeatedly sampled
 *      3) The random state of the sampler
 */
public class HitAndRunSampler {
    private final DirectionSamplingAlgorithm samplingAlgorithm;
    private final SampleSelector selector;
    private final ConvexBodyCache<Vector[]> cache;
    private ConvexBodyCache<Ellipsoid> ellipsoidCache;
    private final boolean isRounding;

    public static class Builder {
        private SampleSelector selector;
        private ConvexBodyCache<Vector[]> cache = new SampleCacheStub<>();
        private ConvexBodyCache<Ellipsoid> ellipsoidCache = new SampleCacheStub<>();
        private DirectionSamplingAlgorithm samplingAlgorithm = new RandomDirectionAlgorithm();

        public Builder(SampleSelector selector) {
            this.selector = Objects.requireNonNull(selector);
        }

        public Builder addSampleCache() {
            cache = new SampleCache();
            return this;
        }

        public Builder addRoundingCache() {
            ellipsoidCache = new EllipsoidCache();
            return this;
        }

        public Builder addRounding(long maxIters) {
            samplingAlgorithm = new RoundingAlgorithm(maxIters);
            return this;
        }

        public HitAndRunSampler build() {
            return new HitAndRunSampler(this);
        }
    }

    private HitAndRunSampler(Builder builder) {
        this(builder.samplingAlgorithm, builder.selector, builder.cache, builder.ellipsoidCache);
    }

    HitAndRunSampler(DirectionSamplingAlgorithm samplingAlgorithm, SampleSelector selector, ConvexBodyCache<Vector[]> cache, ConvexBodyCache<Ellipsoid> ellipsoidCache) {
        this.samplingAlgorithm = samplingAlgorithm;
        this.selector = selector;
        this.cache = cache;
        this.ellipsoidCache = ellipsoidCache;
        this.isRounding = this.samplingAlgorithm instanceof RoundingAlgorithm;
    }

    /**
     * @param body: convex body to sample from
     * @param numSamples: number of samples to retrieve
     * @return an array of numSamples samples from the input body
     */
    public Vector[] sample(ConvexBody body, int numSamples) {
        Validator.assertPositive(numSamples);

        final DirectionSampler directionSampler = samplingAlgorithm.fit(ellipsoidCache.attemptToSetCache(body));

        // When using rounding, the fitted ellipsoid's center can be used as starting point for hit-and-run
        if (isRounding) {
            Ellipsoid ellipsoid = ((EllipsoidSampler) directionSampler).getEllipsoid();

            if (body.isInside(ellipsoid.getCenter())) {
                cache.updateCache(new Vector[]{ellipsoid.getCenter()});
            }

            ellipsoidCache.updateCache(ellipsoid);
        }

        HitAndRun chain = new HitAndRun(cache.attemptToSetCache(body), directionSampler);
        Vector[] samples = selector.select(chain, numSamples);

        cache.updateCache(samples);

        return samples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HitAndRunSampler that = (HitAndRunSampler) o;
        return isRounding == that.isRounding &&
                Objects.equals(samplingAlgorithm, that.samplingAlgorithm) &&
                Objects.equals(selector, that.selector) &&
                Objects.equals(cache, that.cache) &&
                Objects.equals(ellipsoidCache, that.ellipsoidCache);
    }
}
