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
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.EuclideanEllipsoid;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

public class EllipsoidCache implements ConvexBodyCache<Ellipsoid> {
    private Ellipsoid cache = null;

    @Override
    public void updateCache(Ellipsoid toCache) {
        this.cache = toCache;
    }

    @Override
    public ConvexBody attemptToSetCache(ConvexBody body) {
        if (cache == null || body.dim() <= cache.dim()) {
            cache = null;
            return body;
        }

        if (body.dim() != cache.dim() + 1) {
            throw new RuntimeException("Only +1 dimensionality difference supported");
        }

        ConvexBodyWrapper wrapper = new ConvexBodyWrapper(body);

        int curDim = cache.dim();
        int newDim = body.dim();

        Vector center = cache.getCenter().resize(newDim);

        Matrix scale = cache.getScale().resize(newDim, newDim);
        scale.set(curDim, curDim, curDim);
        scale.iScalarMultiply(1.0 + 1.0 / curDim);

        Matrix L = cache.getL().resize(newDim, newDim);
        L.set(curDim, curDim, 1.0);

        Vector D = cache.getD().resize(newDim);
        D.set(curDim, curDim);
        D.iScalarMultiply(1.0 + 1.0 / curDim);

        wrapper.setEllipsoidCache(new EuclideanEllipsoid(center, scale, L, D));

        return wrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EllipsoidCache that = (EllipsoidCache) o;
        return Objects.equals(cache, that.cache);
    }
}
