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

import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;

public class GeodesicSegment {
    private final double lowerBound, upperBound;
    private final Geodesic geodesic;

    GeodesicSegment(Geodesic geodesic, double lowerBound, double upperBound) {

        Validator.assertIsFinite(lowerBound);
        Validator.assertIsFinite(upperBound);


        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be smaller than upper bound.");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.geodesic = geodesic;
    }

    public final double getLowerBound() {
        return lowerBound;
    }

    public final double getUpperBound() {
        return upperBound;
    }

    public Vector getPoint(double proportion) {
        Validator.assertInRange(proportion, 0, 1);
        return geodesic.getPoint(lowerBound + proportion * (upperBound - lowerBound));
    }

    public GeodesicSegment intersect(GeodesicSegment segment) {
        Validator.assertEquals(geodesic, segment.geodesic);

        return new GeodesicSegment(
                geodesic,
                Math.max(lowerBound, segment.lowerBound),
                Math.min(upperBound, segment.upperBound)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeodesicSegment that = (GeodesicSegment) o;
        return Double.compare(that.lowerBound, lowerBound) == 0 &&
                Double.compare(that.upperBound, upperBound) == 0 &&
                Objects.equals(geodesic, that.geodesic);
    }
}
