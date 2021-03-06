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

import utils.LinearAlgebra;

import java.util.*;


/**
 * A polytope that can be updated by adding vertices
 *
 * @author lppeng, enhui
 */
public class ConvexPolytope {
    /**
     * Dimension of the vertices
     */
    private final int dim;

    /**
     * Factorial of the dimension
     */
    private final long dimFactorial;

    /**
     * Bounding box used for random sampling
     */
    private final double[][] boundBox;

    /**
     * Facets of the convex polytope
     */
    private HashSet<Facet> facets;

    /**
     * Create a convex polytope based on a simplex of (dim+1) vertices
     * @param dim dimension of the vertices
     * @param simplex a list of (dim+1) vertices
     * @throws IllegalArgumentException if the number of vertices in the simplex is not (dim+1)
     */
    public ConvexPolytope(int dim, double[][] simplex) {
        if (simplex.length != dim + 1) {
            throw new IllegalArgumentException("Expected number of vertices = " + dim + 1 + ", but get " + simplex.length + " vertices");
        }

        this.dim = dim;

        long _dimFactorial = 1;
        for (int i = 2; i <= dim; i++) {
            _dimFactorial *= i;
        }
        dimFactorial = _dimFactorial;

        // initialize bounding box
        boundBox = new double[dim][2];
        for (int i = 0; i < dim; i++) {
            boundBox[i][0] = Double.MAX_VALUE;
            boundBox[i][1] = Double.MIN_VALUE;
            for (double[] aSimplex : simplex) {
                boundBox[i][0] = Math.min(boundBox[i][0], aSimplex[i]);
                boundBox[i][1] = Math.max(boundBox[i][1], aSimplex[i]);
            }
        }

        Vertex[] vertices = new Vertex[simplex.length];
        for (int i = 0; i < simplex.length; i++) {
            vertices[i] = new Vertex(dim, simplex[i]);
        }

        facets = new HashSet<>();
        for (int i = 0; i < simplex.length; i++) {
            Vertex[] facet = new Vertex[dim];
            int index = -1;
            for (int j = 0; j < simplex.length; j++) {
                if (j == i) {
                    continue;
                }
                facet[++index] = vertices[j];
            }
            // for ith facet, do not include vertex i, instead, treat vertex i as a ref of internal points of the convex polytope
            facets.add(new Facet(dim, facet, vertices[i], true));
        }
    }

    /**
     * Add a vertex to the convex polytope and update the facets
     * @param point the point to be checked
     * @throws IllegalArgumentException if the dim of the point is wrong
     */
    public void addVertex(double[] point) {
        if (point.length != dim) {
            throw new IllegalArgumentException("cannot add a " + point.length + "-dimensional vertex to a " + dim + "-dimensional convex hull"); // Todo: Add a proper Exception here
        }
        // step 1: find all visible facets with respect to the point
        ArrayList<Facet> visibleFacets = new ArrayList<>();
        for (Facet f : facets) {
            if (f.isVisible(point) > 0) {
                visibleFacets.add(f);
            }
        }

        if (visibleFacets.isEmpty()) {
            return;
        }

        // step 2: remove visible facets and find horizon ridges (that connects a visible facet and an invisible facet)
        HashMap<Ridge, Vertex> horizonRidges = new HashMap<Ridge, Vertex>();
        for (Facet f : visibleFacets) {
            facets.remove(f);
            Ridge[] ridges = f.getRidge();
            for (int i = 0; i < ridges.length; i++) {
                Ridge r = ridges[i];
                if (horizonRidges.containsKey(r)) {
                    horizonRidges.remove(r); // each ridge connects two facets, if both facets are visible, it's not a horizon ridge
                } else {
                    horizonRidges.put(r, f.getVertices()[i]);
                }
            }
        }

        // step 3: add new facets
        Vertex v = new Vertex(dim, point);
        for (Ridge r : horizonRidges.keySet()) {
            Facet f = new Facet(r, v, horizonRidges.get(r), true);
            facets.add(f);
        }

        // update bounding box
        for (int i = 0; i < dim; i++) {
            boundBox[i][0] = Math.min(boundBox[i][0], point[i]);
            boundBox[i][1] = Math.max(boundBox[i][1], point[i]);
        }
    }


    /**
     * @return all the facets of the convex polytope
     */
    public HashSet<Facet> getFacets() {
        return facets;
    }

    /**
     * Check whether a point is contained in the convex polytope
     * @param point
     * @return true if it is a internal point, false otherwise
     */
    public boolean containsPoint(double[] point) {
        if (point.length != dim) {
            throw new IllegalArgumentException("cannot judge whether a " + point.length + "-dimensional point is inside a " + dim + "-dimensional convex hull");
        }

        for (Facet f : facets) {
            if (f.isVisible(point) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the volume of the convex polytope
     */
    //Todo: understand the method
    public double volume() {
        if (facets == null || facets.size() < dim + 1) {
            return 0;
        }
        Iterator<Facet> itr = facets.iterator();
        Facet firstFacet = itr.next();
        Vertex v0 = firstFacet.getVertices()[0];

        double volume = 0;
        while (itr.hasNext()) {
            Facet f = itr.next();
            Vertex[] vs = f.getVertices();

            boolean containsV0 = false;
            for (Vertex v : vs) {
                if (v == v0) {
                    containsV0 = true;
                    break;
                }
            }

            if (!containsV0) {
                double[][] matrix = new double[dim][dim];
                for (int i = 0; i < dim; i++) {
                    for (int j = 0; j < dim; j++) {
                        matrix[i][j] = vs[i].getValues()[j] - v0.getValues()[j];
                    }
                }
                volume = volume + Math.abs(LinearAlgebra.determinant(matrix)) / dimFactorial;
            }
        }
        return volume;
    }

    /**
     * Rejection sampling of the convex polytope
     *
     * @return a random point in the convex polytope
     */
    public double[] randomSample() {
        Random r = new Random();
        double[] point = new double[dim];
        do {
            for (int i = 0; i < dim; i++) {
                point[i] = boundBox[i][0] + r.nextDouble() * (boundBox[i][1] - boundBox[i][0]);
            }
        }
        while (!containsPoint(point));
        return point;
    }

    /**
     * @return present the facets of the convex polytope
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(facets.size());
        sb.append(" facets\n");
        for (Facet f : facets) {
            sb.append(f.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
