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

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FacetTest {
    private Facet facet;
    private Vertex[] vertices;
    private Vertex ref;

    @BeforeEach
    void setUp() {
        Vertex v1 = new Vertex(3, new double[]{0,0,0});
        Vertex v2 = new Vertex(3, new double[]{0,0,1});
        Vertex v3 = new Vertex(3, new double[]{0,1,0});
        vertices = new Vertex[3];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        ref = new Vertex(3, new double[]{1,1,1});
        facet = new Facet(3, vertices, ref, true);
    }

    @Test
    void getCoef(){
        assertArrayEquals(new double[]{-1,-0.0,0}, facet.getCoef());
    }

    @Test
    void getOffset(){
        assertEquals(0, facet.getOffset());
    }

    @Test
    void getVertices() {
        assertEquals(vertices, facet.getVertices());
    }

    @Test
    void getRidge() {
        int dim = vertices.length;
        Ridge[] ridges = new Ridge[dim];
        ridges[0] = new Ridge(dim, Arrays.copyOfRange(vertices, 1,3));
        Vertex[] v = new Vertex[2];
        v[0] = new Vertex(3, new double[]{0,0,0});
        v[1] = new Vertex(3, new double[]{0,1,0});
        ridges[1] = new Ridge(dim, v);
        ridges[2] = new Ridge(dim, Arrays.copyOfRange(vertices, 0,2));
        assertArrayEquals(ridges, facet.getRidge());
    }

    @Test
    void getRidge1() {
        Pair<Ridge, Vertex>[] pairs = new Pair[2];
        Vertex[] v = new Vertex[2];
        v[0] = new Vertex(3, new double[]{0,0,0});
        v[1] = new Vertex(3, new double[]{0,1,0});
        pairs[0] = new Pair<>(new Ridge(3, v), vertices[1]);
        pairs[1] = new Pair<>(new Ridge(3, Arrays.copyOfRange(vertices, 0,2)), vertices[2]);
        assertArrayEquals(pairs, facet.getRidge(vertices[0]));
    }

    @Test
    void notVisible() {
        double[] point = new double[]{1,2,3};
        assertEquals(-1, facet.isVisible(point));
    }

    @Test
    void isVisible(){
        double[] point = new double[]{-1,2,3};
        assertEquals(1, facet.isVisible(point));
    }

    @Test
    void onFacet(){
        double[] point = new double[]{0,2,3};
        assertEquals(0, facet.isVisible(point));
    }
}