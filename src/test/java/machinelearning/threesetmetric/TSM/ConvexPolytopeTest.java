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

package machinelearning.threesetmetric.TSM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvexPolytopeTest {
    private ConvexPolytope convexPolytope;
    private double[][] simplex;

    @BeforeEach
    void setUp(){
        simplex = new double[4][];
        simplex[0] = new double[]{0,0,0};
        simplex[1] = new double[]{0,0,1};
        simplex[2] = new double[]{0,1,0};
        simplex[3] = new double[]{1,0,0};
        convexPolytope = new ConvexPolytope(3, simplex);
    }

    @Test
    void addVertex_fail() {
        convexPolytope.addVertex(new double[]{0.1,0.1,0.1});
        assertEquals(4,convexPolytope.getFacets().size());
    }

    @Test
    void addVertex_number_of_facets() {
        convexPolytope.addVertex(new double[]{1,1,1});
        assertEquals(6,convexPolytope.getFacets().size());
    }

    @Test
    void addVertex_containVertex() {
        convexPolytope.addVertex(new double[]{1,1,1});
        assertEquals(true, convexPolytope.containsPoint(new double[]{1,1,1}));
    }

    @Test
    void getFacets() {
        assertEquals(4, convexPolytope.getFacets().size());
    }

    @Test
    void containsPoint_internalPoint() {
        assertEquals(true, convexPolytope.containsPoint(new double[]{0.25,0.25,0.25}));
    }

    @Test
    void containsPoint_onBoundary() {
        assertEquals(true, convexPolytope.containsPoint(new double[]{0,0.5,0.5}));

    }

    @Test
    void containsPoint_externalPoint() {
        assertEquals(false, convexPolytope.containsPoint(new double[]{1,1,1}));

    }

    @Test
    void randomSample() {
        assertEquals(true, convexPolytope.containsPoint(convexPolytope.randomSample()));
    }
}