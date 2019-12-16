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

package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.EuclideanEllipsoid;
import machinelearning.classifier.margin.HyperPlane;
import utils.linalg.EigenvalueDecomposition;
import utils.linalg.Vector;

public interface EuclideanConvexBody extends ConvexBody {
    @Override
    default Manifold getManifold() {
        return EuclideanSpace.getInstance();
    }

    default Ellipsoid getContainingEllipsoid() {
        return new EuclideanEllipsoid(dim(), getRadius());
    }

    default boolean attemptToReduceEllipsoid(Ellipsoid ellipsoid) {
        Vector center = ellipsoid.getCenter();
        if (!isInside(center) && ellipsoid.cut(getSeparatingHyperplane(center)))
            return true;

        EigenvalueDecomposition decomposition = new EigenvalueDecomposition(ellipsoid.getScale());

        for (int i = 0; i < dim(); i++) {
            if (decomposition.getEigenvalue(i) <= 0) {
                throw new RuntimeException("Found non-positive eigenvalue: " + decomposition.getEigenvalue(i));
            }

            double factor = Math.sqrt(decomposition.getEigenvalue(i)) / (dim() + 1.);
            Vector direction = decomposition.getEigenvector(i).iScalarMultiply(factor);

            Vector extreme = center.add(direction);
            if (!isInside(extreme) && ellipsoid.cut(getSeparatingHyperplane(extreme)))
                return true;

            extreme = center.subtract(direction);
            if (!isInside(extreme) && ellipsoid.cut(getSeparatingHyperplane(extreme)))
                return true;
        }

        return false;
    }

    /**
     * @return the radius R of any ball containing {@code this} convex body
     */
    double getRadius();

    /**
     * Computes a separating hyperplane between an data point x and {@code this} convex body. The point x is guaranteed
     * to have a positive margin, while any point on {@code this} convex body will have a non-positive margin.
     *
     * @param x: a point
     * @return a separating hyperplane
     * @throws RuntimeException if point is inside polytope
     */
    HyperPlane getSeparatingHyperplane(Vector x);
}
