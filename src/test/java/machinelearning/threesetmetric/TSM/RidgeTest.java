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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RidgeTest {
    private Ridge ridge;
    private Vertex[] vertices;

    @BeforeEach
    void setUp(){
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{1,1,1});
        vertices = new Vertex[]{vertex_1, vertex_2};
        ridge = new Ridge(3, vertices);
    }

    @Test
    void ridge_constructorException(){
        assertThrows(IllegalArgumentException.class, () -> new Ridge(2, vertices));
    }

    @Test
    void getVertices() {
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{1,1,1});
        Vertex[] v = new Vertex[]{vertex_1, vertex_2};
        assertEquals(Arrays.deepToString(v), Arrays.deepToString(ridge.getVertices()));
    }

    @Test
    void equals() {
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{1,1,1});
        Ridge r = new Ridge(3, new Vertex[]{vertex_1, vertex_2});
        assertEquals(true, ridge.equals(r));
    }

    @Test
    void unEquals() {
        Vertex vertex_1 = new Vertex(3, new double[]{0,0,0});
        Vertex vertex_2 = new Vertex(3, new double[]{0,1,1});
        Ridge r = new Ridge(3, new Vertex[]{vertex_1, vertex_2});
        assertEquals(false, ridge.equals(r));
    }

}