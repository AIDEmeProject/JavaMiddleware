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

package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.euclidean.PolyhedralCone;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class UnitSpherePolyhedralCone implements ConvexBody {

    private PolyhedralCone cone;

    public UnitSpherePolyhedralCone(PolyhedralCone cone) {
        this.cone = cone;
    }

    @Override
    public int dim() {
        return cone.dim();
    }

    @Override
    public boolean isInside(Vector point) {
        return cone.isInside(point) && Math.abs(point.squaredNorm() - 1) < 1e-12;
    }

    @Override
    public Vector getInteriorPoint() {
        return cone.getInteriorPoint().iNormalize(1.0);
    }

    @Override
    public GeodesicSegment computeIntersection(Geodesic geodesic) {
        if (!cone.isInside(geodesic.getCenter()))
            throw new RuntimeException("Geodesic center outside cone.");

        Matrix A = cone.getMatrix();

        Vector a = A.multiply(geodesic.getCenter());
        Vector b = A.multiply(geodesic.getVelocity());

        double lower = -Math.PI, upper = Math.PI;

        for (int i = 0; i < a.dim(); i++) {
            double angle = Math.atan2(-a.get(i), b.get(i));
            lower = Math.max(lower, angle);
            upper = Math.min(upper, angle + Math.PI);
        }

        if (lower >= upper)
            throw new RuntimeException("Empty intersection");

        return geodesic.getSegment(lower, upper);
    }

    @Override
    public Manifold getManifold() {
        return UnitSphere.getInstance();
    }

    @Override
    public boolean attemptToReduceEllipsoid(Ellipsoid ellipsoid) {
        //TODO: implement this
        return false;
    }

    @Override
    public Ellipsoid getContainingEllipsoid() {
        //TODO: implement this
        return null;
    }
}
