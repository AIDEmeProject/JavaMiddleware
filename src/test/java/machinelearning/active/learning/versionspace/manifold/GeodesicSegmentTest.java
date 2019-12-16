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
import org.mockito.Mockito;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GeodesicSegmentTest {
    private Geodesic geodesic;
    private GeodesicSegment segment;

    @BeforeEach
    void setUp() {
        geodesic = mock(Geodesic.class, Mockito.withSettings()
                .useConstructor(Vector.FACTORY.zeros(2), Vector.FACTORY.make(1,2))
                .defaultAnswer(Mockito.CALLS_REAL_METHODS));
        segment = new GeodesicSegment(geodesic, -1, 2);
    }

    @Test
    void constructor_leftBoundEqualsNegativeInfinity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GeodesicSegment(geodesic, Double.NEGATIVE_INFINITY, 1));
    }

    @Test
    void constructor_rightBoundEqualsPositiveInfinity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GeodesicSegment(geodesic, 0, Double.POSITIVE_INFINITY));
    }

    @Test
    void constructor_leftBoundEqualsRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GeodesicSegment(geodesic, 1, 1));
    }

    @Test
    void constructor_leftBoundLargerThanRightBound_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new GeodesicSegment(geodesic,2, 1));
    }

    @Test
    void getPoint_negativeProportion_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> segment.getPoint(-1));
    }

    @Test
    void getPoint_largerThanOneProportion_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> segment.getPoint(2));
    }

    @Test
    void getPoint_zeroProportion_getLeftExtremeFromGeodesic() {
        segment.getPoint(0);
        verify(geodesic).getPoint(-1);
    }

    @Test
    void getPoint_OneProportion_getRightExtremeFromGeodesic() {
        segment.getPoint(1);
        verify(geodesic).getPoint(2);
    }

    @Test
    void getPoint_halfProportion_getCorrectPointFromGeodesic() {
        segment.getPoint(0.5);
        verify(geodesic).getPoint(0.5);
    }

    @Test
    void intersect_differentGeodesics_throwsError() {
        Geodesic g = mock(Geodesic.class);
        assertThrows(RuntimeException.class, () -> segment.intersect(g.getSegment(0, 1)));
    }

    @Test
    void intersect_disjointGeodesic_throwsError() {
        assertThrows(RuntimeException.class, () -> segment.intersect(geodesic.getSegment(4, 5)));
    }

    @Test
    void intersect_intersectingGeodesic_returnsExpectedGeodesic() {
        GeodesicSegment s2 = geodesic.getSegment(1, 3);
        assertEquals(geodesic.getSegment(1, 2), segment.intersect(s2));
    }
}