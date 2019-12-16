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

import java.util.Arrays;


/**
 * This class creates a ridge (or (dim-2)-face) based on a list of vertices
 *
 * @author lppeng, enhui
 */


public class Ridge {
    /**
     * The (dim-1) vertices that define the ridge
     */
    private final Vertex[] vertices;

    /**
     * Create a ridge based on the vertices, and check the number of vertices against the dimensionality of the space
     *
     * @param dim      the dimensionality of the space
     * @param vertices the vertices that define the ridge
     * @throws IllegalArgumentException wrong number of vertices
     */
    public Ridge(int dim, Vertex[] vertices) {
        if (vertices.length != dim - 1) {
            throw new IllegalArgumentException("Expected number of vertices = " + (dim - 1) + ", but get " + vertices.length + " vertices");
        }
        this.vertices = vertices;
        Arrays.sort(this.vertices);
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    /**
     * Specify the equal relationship for ridge
     *
     * @param a a ridge
     * @return true if two ridges are equal, false otherwise
     */
    public boolean equals(Object a) {
        if (this == a) {
            return true;
        }
        if (a == null || this.getClass() != a.getClass()) {
            return false;
        }
        Ridge _a = (Ridge) a;
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].compareTo(_a.vertices[i]) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the same hash code for "equal" ridges by using "deepHashCode" for an array of arrays
     *
     * @see java.util.Arrays
     *
     * @return a hash code for each ridge
     */
    public int hashCode() {
//        int result = 0;
//        for (Vertex vertice : vertices) {
//            result = result * 31 + vertice.hashCode();
//        }
//        return result;

        return Arrays.deepHashCode(vertices);
    }



    public String toString() {
        return Arrays.toString(vertices);
    }
}
