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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class GeodesicTest {
    protected Vector center;
    protected Vector direction;
    protected Geodesic geodesic;

    @BeforeEach
    void setUp() {
        center = Vector.FACTORY.make(3, -2);
        direction = Vector.FACTORY.make(-1, 1);
        geodesic = getInstance(center, direction);
    }

    public abstract Geodesic getInstance(Vector center, Vector direction);

    @Test
    void constructor_CenterAndDirectionHaveIncompatibleDimensions_ThrowsException() {
        assertThrows(RuntimeException.class, () -> getInstance(Vector.FACTORY.make(2, 2), Vector.FACTORY.make(1)));
    }

    @Test
    void constructor_DirectionIsZeroVector_ThrowsException() {
        assertThrows(RuntimeException.class, () -> getInstance(Vector.FACTORY.make(1, 2), Vector.FACTORY.zeros(2)));
    }

    @Test
    void getDim_TwoDimensionalCenterVector_ReturnsTwo() {
        assertEquals(center.dim(), geodesic.dim());
    }

    @Test
    void getPoint_PositionEqualsToZero_CenterIsReturned() {
        assertEquals(center, geodesic.getPoint(0.));
    }

    @Test
    public abstract void getPoint_PositionDifferentFromZero_CorrectPointIsReturned();

    @Test
    void getSegment_infiniteLeftBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(Double.NEGATIVE_INFINITY, 1));
    }

    @Test
    void getSegment_infiniteRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(Double.POSITIVE_INFINITY, 1));
    }

    @Test
    void getSegment_leftBoundEqualsRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(1, 1));
    }

    @Test
    void getSegment_leftBoundLargerThanRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> geodesic.getSegment(2, 1));
    }

    @Test
    void getSegment_validInput_returnsExpectedGeodesicSegment() {
        GeodesicSegment segment = geodesic.getSegment(-2, 3);
        assertEquals(new GeodesicSegment(geodesic, -2, 3), segment);
    }
}