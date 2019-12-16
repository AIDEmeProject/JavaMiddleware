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
import org.apache.commons.math3.util.Precision;
import utils.LinearAlgebra;

import java.util.Arrays;

import static org.apache.commons.math3.util.MathArrays.linearCombination;

/**
 * This class create a facet (or (n-1)-face) based on a list of vertices
 *
 * The facet can be explicitly represented as a hyperplane that satisfies coef * x + offset = 0
 *
 * @author lppeng, enhui
 *
 */


public class Facet {
    /**
     * The (dim) vertices that define the facet
     */
    private final Vertex[] vertices;

    /**
     * Coefficients of the facet
     */
    private final double[] coef;

    /**
     * Offset of the facet
     */
    private double offset;

    /**
     * Create a facet based on the vertices, and check the number of vertices against the dimensionality of the space
     *
     * @param dim:      the dimensionality of the space
     * @param vertices: the vertices that define the facet
     * @param ref:      a reference point
     * @param isInside: the reference point is inside or outside
     * @throws IllegalArgumentException wrong number of vertices
     */
    public Facet(int dim, Vertex[] vertices, Vertex ref, boolean isInside) {
        if (vertices.length != dim) {
            throw new IllegalArgumentException("Expected number of vertices = " + dim + ", but get " + vertices.length + " vertices");
        }
        this.vertices = vertices;
        Arrays.sort(this.vertices);

        double[][] matrix = new double[dim][dim];
        Vertex v0 = vertices[0]; // the first vertex
        for (int j = 0; j < dim; j++) {
            matrix[0][j] = -v0.getValues()[j];
        }
        for (int i = 1; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                // each row of the matrix represents a ridge in the facet
                matrix[i][j] = vertices[i].getValues()[j] - v0.getValues()[j];
            }
        }

        coef = new double[dim];
        double _offset = 0;
        for (int i = 0; i < dim; i++) {
            //Todo: replace the "createSubMatrix" and "determinant" functions by more accurate functions from the existing packages
            coef[i] = ((i & 1) == 0 ? 1 : -1) * LinearAlgebra.determinant(LinearAlgebra.createSubMatrix(matrix, 0, i));
            // offset = - coef * v_0
            _offset += matrix[0][i] * coef[i];
        }
        offset = _offset;

        if ((isVisible(ref.getValues()) > 0 && isInside) || (!(isVisible(ref.getValues()) > 0) && !isInside)) {
            for (int i = 0; i < coef.length; i++) {
                coef[i] = -coef[i];
            }
            offset = -offset;
        }
    }


    /**
     * Create a facet based on the ridges, and check the number of vertices against the dimensionality of the space
     *
     * @param r: the ridges that contribute to define the facet
     * @param v: the last element in the matrix of vertices combined with the ridges to define the facet
     * @param ref: a reference point
     * @param isInside: the reference point is inside or outside
     * @throws IllegalArgumentException wrong number of vertices
     */
    public Facet(Ridge r, Vertex v, Vertex ref, boolean isInside){
        vertices = new Vertex[r.getVertices().length + 1];
        System.arraycopy(r.getVertices(), 0, vertices, 0, r.getVertices().length);
        vertices[r.getVertices().length] = v;
        Arrays.sort(this.vertices);

        double[][] matrix = new double[vertices.length][vertices.length];
        Vertex v0 = vertices[0]; // the first vertex
        for (int j = 0; j < vertices.length; j++) {
            matrix[0][j] = -v0.getValues()[j];
        }
        for (int i = 1; i < vertices.length; i++) {
            for (int j = 0; j < vertices.length; j++) {
                matrix[i][j] = vertices[i].getValues()[j] - v0.getValues()[j];
            }
        }
        coef = new double[vertices.length];
        double _offset = 0;
        for (int i = 0; i < vertices.length; i++) {
            coef[i] = ((i & 1) == 0 ? 1 : -1) * LinearAlgebra.determinant(LinearAlgebra.createSubMatrix(matrix, 0, i));
            _offset += matrix[0][i] * coef[i];
        }
        offset = _offset;

        if ((isVisible(ref.getValues()) > 0 && isInside) || (!(isVisible(ref.getValues()) > 0) && !isInside)) {
            for (int i = 0; i < coef.length; i++) {
                coef[i] = -coef[i];
            }
            offset = -offset;
        }
    }


    /**
     * @return coefficient
     */
    public double[] getCoef(){
        return coef;
    }

    /**
     * @return offset
     */
    public double getOffset(){
        return offset;
    }

    /**
     * @return vertices of the facet
     */
    public Vertex[] getVertices() {
        return vertices;
    }

    /**
     * @return ridges of the facet
     */
    public Ridge[] getRidge() {
        Ridge[] ridges = new Ridge[vertices.length];
        for (int i = 0; i < vertices.length; i++) { // for ith ridge, do not include vertex i
            Vertex[] ridge = new Vertex[vertices.length - 1];
            int index = -1;
            for (int j = 0; j < vertices.length; j++) {
                if (j == i) {
                    continue;
                }
                ridge[++index] = vertices[j];
            }
            ridges[i] = new Ridge(vertices.length, ridge);
        }
        return ridges;
    }


    /**
     * @param v : a vertex in the facet
     * @return all the <ridge, vertex> pairs such that vertex list do not include v and ridge consists of the vertices excluding vertex
     * @throws IllegalArgumentException if the input point is not a vertex of the facet
     */

    public Pair<Ridge, Vertex>[] getRidge(Vertex v) {
        Pair<Ridge, Vertex>[] ridges = new Pair[vertices.length - 1];
        int pos = -1;
        for (int i = 0; i < vertices.length; i++) {
            Vertex vertex = vertices[i];
            //if (vertex.equals(v)) {
            if(vertex.compareTo(v)==0){
                pos = i;
                break;
            }
        }
        if (pos == -1) {
            throw new IllegalArgumentException(v.toString() + " is not a vertex of facet: " + this.toString());
        }

        int ridgeCount = 0;
        for (int i = 0; i < vertices.length; i++) {// for each ridge, do not include vertex i
            if (i != pos) {
                Vertex[] ridge = new Vertex[vertices.length - 1];
                int index = -1;
                for (int j = 0; j < vertices.length; j++) {
                    if (j == i) {
                        continue;
                    }
                    ridge[++index] = vertices[j];
                }
                ridges[ridgeCount] = new Pair<>(new Ridge(vertices.length, ridge), vertices[i]);
                ridgeCount++;
            }
        }
        return ridges;
    }

    /**
     * @param point a point to be checked
     * @return sign of the point corresponding to the facet. 1 means positive(visible), -1 means negative and 0 means on the facet
     * @throws IllegalArgumentException if the dim of point doesn't match the coef's dim
     */

    public int isVisible(double[] point) {
        if (point.length != coef.length) {
            throw new IllegalArgumentException("point dim = " + point.length + ", facet dim =" + coef.length);
        }
        // "innerProduct" defined in this program isn't accurate enough for the float-numbers with long decimal digits
        // (LinearAlgebra.innerProduct(point, coef) + offset > 0 ? 1: -1)
        //return Precision.compareTo( linearCombination(point, coef) + offset, 0, Configuration.getTolerance());
        //Todo: predefined the tolerance?
        return Precision.compareTo( linearCombination(point, coef) + offset, 0, 1e-11);
    }


//    public boolean isOnFacet(double[] point) throws IOException {
//        if (point.length != coef.length) {
//            throw new IOException("point dim = " + point.length + ", facet dim =" + coef.length);
//        }
//        return (Precision.compareTo( linearCombination(point, coef) + offset, 0, 1e-11) == 0);
//    }

    /**
     * @return the list of vertices in the form of a circle
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex v : vertices) {
            sb.append(v.toString());
            sb.append("->");
        }
        sb.append(vertices[0]);
        return sb.toString();
    }

    /**
     * Return the same hash code for "equal" facet by using "deepHashCode" for an array of arrays
     *
     * @see java.util.Arrays
     *
     * @return a hash code for each facet
     */
    public int hashCode() {
//        int result = 0;
//        for (Vertex vertice : vertices) {
//            result = result * 31 + vertice.hashCode();
//        }
//        return result;
        return Arrays.deepHashCode(vertices);
    }


    /**
     * @param a a facet
     * @return true for the facets filled with the same vertices, false otherwise
     */
    public boolean equals(Object a) {
        if (this == a) {
            return true;
        }
        if (a == null || this.getClass() != a.getClass()) {
            return false;
        }
        Facet _a = (Facet) a;
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].compareTo(_a.vertices[i]) != 0) {
                return false;
            }
        }
        return true;
    }

}

