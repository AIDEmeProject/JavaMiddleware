package machinelearning.threesetmetric.TSM;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Each negative region(in the shape of convex cone) has a dual simplex
 *
 * @author lppeng, enhui
 */

public class PointWiseComplementConvexHull {
    /**
     * Dimension of the vertices
     */
    private final int dim;

    /**
     * Apex of the convex cone
     */
    private Vertex negVertex;

    /**
     * Facets of the dual simplex
     */
    private HashSet<Facet> facets;


    /**
     * Construct a convex cone based on a negative point and the set of positive points
     * @param dim
     * @param negVertex apex of the convex cone
     * @param posVertex vertices from the convex polytope
     * @throws IllegalArgumentException if the convex polytope does not exist
     */
    public PointWiseComplementConvexHull(int dim, double[] negVertex, double[][] posVertex) {
        this.dim = dim;
        if (posVertex.length < dim) {
            throw new IllegalArgumentException("Expecting " + dim + " vertices but was given " + posVertex.length);
        }
        this.negVertex = new Vertex(dim, negVertex);

        Vertex[] vertices = new Vertex[dim + 1];
        vertices[0] = this.negVertex;
        for (int i = 1; i <= dim; i++) {
            vertices[i] = new Vertex(dim, posVertex[i - 1]);
        }

        facets = new HashSet<>();
        for (int i = 1; i <= dim; i++) { // for ith facet, do not include the i-th positive vertex
            Vertex[] facet = new Vertex[dim];
            facet[0] = this.negVertex; // each facet always has the negative vertex
            int index = 0;
            for (int j = 1; j <= dim; j++) {
                if (j == i) {
                    continue;
                }
                facet[++index] = vertices[j];
            }
            // the ref ith vertex should be a external point with respect to the new facet
            facets.add(new Facet(dim, facet, vertices[i], false));
        }

        //if there are more positive points, add them as the vertices
        //Todo: understand how the positive points are added to the dual simplex
        for (int i = dim; i < posVertex.length; i++) {
            if (posVertex[i] != null) {
                addVertex(posVertex[i]);
            }
        }
    }

    /**
     * Construct a convex cone based on a negative point and the existing convex polytope
     * @param dim
     * @param negVertex apex of the convex cone
     * @param positiveRegion the convex polytope
     */
    public PointWiseComplementConvexHull(int dim, double[] negVertex, ConvexPolytope positiveRegion) {
        this.dim = dim;
        this.negVertex = new Vertex(dim, negVertex);
        facets = new HashSet<>();
        // step 1: find all visible facets
        ArrayList<Facet> visibleFacets = new ArrayList<>();
        for (Facet f : positiveRegion.getFacets()) {
            if (f.isVisible(negVertex) > 0) {
                visibleFacets.add(f);
            }
        }

        if(visibleFacets.isEmpty()){
            throw new IllegalArgumentException("The positive point" + negVertex + "can't be a negative point");
        }
        // step 2: find the horizon ridges
        HashMap<Ridge, Vertex> horizonRidges = new HashMap<>();
        for (Facet f : visibleFacets) {
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
        for (Ridge r : horizonRidges.keySet()) {
            // the ref point is inside the dual simplex but outside the convex cone
            Facet f = new Facet(r, this.negVertex, horizonRidges.get(r), false);
            facets.add(f);
        }
    }

    /**
     * Add a positive vertex to the dual simplex and update the facets
     * @param point the point to be checked
     * @throws IllegalArgumentException if the dim of the point is wrong
     */
    public void addVertex(double [] point) {
        if(point.length!=dim) {
            throw new IllegalArgumentException("cannot add a " + point.length +"-dimensional vertex to a " + dim + "-dimensional convex hull");
        }
        // step 1: find all visible facets
        ArrayList<Facet> invisibleFacets = new ArrayList<Facet>();
        for(Facet f:facets) {
            if(f.isVisible(point) < 0) {
                invisibleFacets.add(f);
            }
        }
        if(invisibleFacets.isEmpty()) {
            return;
        }
        else if(invisibleFacets.size()==facets.size()) {
            throw new IllegalArgumentException("a positive point cannot be inside the negative region: " + negVertex);
        }
        else {
            // step 2: remove visible facets and find horizon ridges (that connects a visible facet and an invisible facet)
            HashMap<Ridge, Vertex> horizonRidges = new HashMap<Ridge,Vertex>();
            for(Facet f:invisibleFacets) {
                facets.remove(f);
                Pair<Ridge, Vertex> [] ridges = f.getRidge(negVertex);
                for(int i=0; i<ridges.length; i++) {
                    Pair<Ridge, Vertex> p = ridges[i];
                    Ridge r = p.getKey();
                    if(horizonRidges.containsKey(r)) {
                        horizonRidges.remove(r); // each ridge connects two facets, if both facets are visible, it's not a horizon ridge
                    }
                    else {
                        horizonRidges.put(r,p.getValue());
                    }
                }
            }
            // step 3: add new facets
            Vertex v = new Vertex(dim, point);
            Iterator<Ridge> ridgeItr = horizonRidges.keySet().iterator();
            while(ridgeItr.hasNext()) {
                Ridge r = ridgeItr.next();
                Facet f = new Facet(r,v,horizonRidges.get(r), false);
                facets.add(f);
            }
        }
    }

    /**
     * Check where the point is contained by the convex cone
     * @param point
     * @return true if the point is inside the convex cone
     * @throws IllegalArgumentException if the dim of the point is wrong
     */
    public boolean containsPoint(double[] point) {
        if (point.length != dim) {
            throw new IllegalArgumentException("cannot judge whether a " + point.length + "-dimensional point is inside a " + dim + "-dimensional negative region");  // Todo: Add a proper Exception here
        }
        for (Facet f : facets) {
            if (f.isVisible(point) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the facets of the convex cone(only the facets that include the apex)
     */
    public HashSet<Facet> getFacets(){ return facets;}


    /**
     * @return the facets of the convex cone
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

