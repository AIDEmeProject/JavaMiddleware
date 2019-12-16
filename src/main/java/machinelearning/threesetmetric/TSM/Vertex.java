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
 * This class stores a vertex in the TSM model
 *
 * @author lppeng, enhui
 */


public class Vertex implements Comparable<Vertex> {
    /**
     * Feature values of a vertex
     */
    private final double[] values;

    /**
     * Create a vertex based on the values on each dimension, and check the number of values against the dimensionality of the space
     *
     * @param dim    the dimensionality of the space
     * @param values the feature values on every dimension
     * @throws IllegalArgumentException inconsistent dimensions with those of the expected vectors
     */
    public Vertex(int dim, double[] values) {
        if (values.length != dim) {
            throw new IllegalArgumentException("Expected dim = " + dim + ", but get value dim = " + values.length);
        }
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }

    /**
     * Specify the order of two vertices
     * @see java.lang.Comparable
     * @since 1.2
     *
     * @param  o a vertex
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     *         Once an element is larger(smaller) than that of the latter vertex, it returns 1(-1).
     *         Otherwise, the two vertices are identical.
     */
    public int compareTo(Vertex o) {
        for (int i = 0; i < values.length; i++) {
            if (this.values[i] > o.values[i]) {
                return 1;
            } else if (this.values[i] < o.values[i]) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Return the same hash code for "equal" vertices
     *
     * @return a hash code for each vertex
     */
    public int hashCode() {
//        int result = 0;
//        for (double value : values) {
//            result = result * 31 + Double.valueOf(value).hashCode();
//        }
//        return result;
        return Arrays.hashCode(values);
    }

    public String toString() {
        return Arrays.toString(values);
    }
}

