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

package machinelearning.active.learning.versionspace.manifold.cache;


import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import utils.linalg.Vector;

import java.util.Arrays;

/**
 * The SampleCache is a module for caching samples from the {@link machinelearning.active.learning.versionspace.LinearVersionSpace}
 * object. One of the inherent problems with the LinearVersionSpace sampling algorithm is in the computation of a first
 * initial sample inside the Version Space, which requires solving a Linear Programming problem. In the Active Learning scenario,
 * we can alleviate this problem through a caching procedure: every time the sampling algorithm is called, we cache the samples
 * drawn from the version space. In the next call of the sampling procedure, we check whether one of the previous samples is
 * already inside the current version space, avoid the Linear Programming step most of time. This method works well in
 * practice since the Version Space does not tend to reduce abruptly from on iteration to another.
 */
public class SampleCache implements ConvexBodyCache<Vector[]> {
    /**
     * cache samples
     */
    private Vector[] cachedSamples;

    /**
     * Initialize object with a empty cache
     */
    public SampleCache() {
        cachedSamples = new Vector[0];
    }

    /**
     * Replace current cache with a new sample
     *
     * @param samples new sample to cache
     */
    public void updateCache(Vector[] samples) {
        cachedSamples = samples;
    }

    /**
     * This method checks whether any one of the cached samples are inside the input {@link ConvexBody}. If so, we return
     * a "wrapped convex body", which returns the cached sample whenever the getInteriorPoint() method is called. Otherwise,
     * the input object is returned without modification.
     *
     * @param convexBody: a convex body object
     * @return the wrapped convex body
     */
    @Override
    public ConvexBody attemptToSetCache(ConvexBody convexBody) {
        for (Vector cachedSample : cachedSamples){
            // TODO: make this line testable / push this dependency logic to KernelVersionSpace somehow ?
            cachedSample = cachedSample.resize(convexBody.dim());

            if (convexBody.isInside(cachedSample)) {
                ConvexBodyWrapper wrap = new ConvexBodyWrapper(convexBody);
                wrap.setInteriorPointCache(cachedSample);
                return wrap;
            }
        }

        return convexBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleCache that = (SampleCache) o;
        return Arrays.equals(cachedSamples, that.cachedSamples);
    }
}
