package machinelearning.threesetmetric.TSM;

import data.DataPoint;
import data.LabeledPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


/**
 * This class create a TSM partition on a subspace spanned by a subset of features
 *
 * @author lppeng, enhui
 */

public class TsmLearner extends CatTSM {

    /**
     * Reservoir of initial points in convex region
     */
    private double[][] convex;

    /**
     * Count of points in convex region
     */
    private int convexCount;

    /**
     * Reservoir of initial points in concave region
     */
    private ArrayList<double[]> concave;

    /**
     * True if the convex region has been initialized, false otherwise
     */
    private boolean convexInitialized = false;

    /**
     * True if the concave region has been initialized, false otherwise
     */
    private boolean concaveInitialized = false;

    /**
     * Dim of vertices
     */
    private final int dim;

    /**
     * Convex polytope
     */
    private ConvexPolytope convexRegion;

    /**
     * The union of convex cones
     */
    private final ArrayList<PointWiseComplementConvexHull> concaveRegions;

    /**
     * Vertices of the convex polytope and convex cone
     */
    private HashSet<double[]> vertices;

    // one dimension TSM
    /**
     * Three-set partition on 1-dim space
     */
    private OneDimTSM oneDimTSM;

    /**
     * Create Three-Set Partition for any dimensional space
     * @param dim dim of vertices
//     * @param dataPoints points for evaluation
     */
    public TsmLearner(int dim) {
        this.dim = dim;

        convex = new double[dim + 1][];
        convexCount = 0;
        concave = new ArrayList<>();
        concaveRegions = new ArrayList<>();
        oneDimTSM = new OneDimTSM();
    }

    /**
     * Update Three-Set Partition for numerical attributes when the positive region is convex
     * @param labeledSamples labeled point projected onto a subspace
     * @throws IllegalArgumentException if a negative point is found in positive region and vice versa
     */
    public void updatePosRatio(Collection<LabeledPoint> labeledSamples) {
        for (LabeledPoint t : labeledSamples) {
            double[] point = t.getData().toArray();



            if(dim==1) {
                System.out.println("--in LEARNER--");
                System.out.println(t.getData());
                System.out.println(t.getLabel().asSign());
                System.out.println("");
                oneDimTSM.updatePos(point[0], t.getLabel().asSign());
            }else {
                // check the label on a subspace
                if (t.getLabel().isPositive()) {
                    // check whether a positive point(including the boundary) is inside the concave region or not
                    if(isInConcaveRegion(point)){
                        throw new IllegalArgumentException("Pos in concave : " + Arrays.toString(point));
                    }
                    updateConvexRegion(point);
                } else {
                    // check whether a negative point (including the boundary) is inside the convex region or not
                    if(isInConvexRegion(point)){
                        throw new IllegalArgumentException("Neg in convex : " + Arrays.toString(point));
                    }
                    updateConcaveRegion(point);
                }

                // get the vertices of both pos and neg regions
                vertices = getVertices(convexRegion, concaveRegions);
            }
        }

    }

    /**
     * Update Three-set Partition for numerical attributes when the negative region is convex
     * @param labeledSamples labeled point projected onto a subspace
     * @throws IllegalArgumentException if a negative point is found in positive region and vice versa
     */
    public void updateNegRatio(Collection<LabeledPoint> labeledSamples) {
        for(LabeledPoint t: labeledSamples) {
            double[] point = t.getData().toArray();
            if(dim==1) {
                oneDimTSM.updateNeg(point[0], t.getLabel().asSign());
            }else {
                if (t.getLabel().isNegative()) {
                    // check whether a negative point(including the boundary) is inside the concave region or not
                    if(isInConcaveRegion(point)){
                        throw new IllegalArgumentException("Neg in concave : " + Arrays.toString(point));
                    }
                    updateConvexRegion(point);
                } else {
                    // check whether a positive point( including the boundary) is inside the convex region or not
                    if(isInConvexRegion(point)){
                        throw new IllegalArgumentException("Pos in convex : " + Arrays.toString(point));
                    }

                    updateConcaveRegion(point);
                }

                // get the vertices of both pos and neg regions
                vertices = getVertices(convexRegion, concaveRegions);
            }
        }
    }

    /**
     * Update the convex ploytope
     * @param point point to be used for TSM
     */
    private void updateConvexRegion(double[] point) {
        // convexInitialized true means the convex hull has been created
        if (!convexInitialized) {
            // remove the duplicates from pos points
            boolean isDuplicates = findDuplicates(convex, point);
            if (!isDuplicates) {
                convex[convexCount++] = point;
            }

            // if this point is the (dim)-th positive sample and there already are some negative samples
            if (convexCount == dim && !concave.isEmpty()) {
                // initialize the negative regions
                for (double[] negPoint : concave) {
                    concaveRegions.add(new PointWiseComplementConvexHull(dim, negPoint, convex));
                }
                concaveInitialized = true;
                concave.clear();
            } else if (convexCount == dim + 1) {
                convexRegion = new ConvexPolytope(dim, convex);
                convexInitialized = true;
                if (concaveInitialized) {
                    for (PointWiseComplementConvexHull nr : concaveRegions) {
                        nr.addVertex(point);
                    }
                }
            }
        } else if (vertices != null && findDuplicates(vertices, point)) {
            //todo: check whether this step is necessary

        } else {
            convexRegion.addVertex(point);
            for (PointWiseComplementConvexHull nr : concaveRegions) {
                nr.addVertex(point);
            }
        }
    }

    /**
     * Update the convex cones
     * @param point point to be used for TSM
     */
    private void updateConcaveRegion(double[] point) {
        // concaveInitialized true means the convex cone has been created
        if (!concaveInitialized) {
            if (convexCount < dim) {
                // record the negative points until the convex polytope has been built
                concave.add(point);
            } else {
                // in this case, convex polytope has existed
                concaveRegions.add(new PointWiseComplementConvexHull(dim, point, convex));
                concaveInitialized = true;
            }
        } else {
            boolean createNew = true;
            for (PointWiseComplementConvexHull nr : concaveRegions) {
                if (nr.containsPoint(point)) {
                    // one of the convex cones contains this point
                    createNew = false;
                    break;
                }
            }
            if (createNew) {
                if (convexRegion != null) {
                    concaveRegions.add(new PointWiseComplementConvexHull(dim, point, convexRegion));
                } else {
                    // although no positive region exists, there must be a facet
                    concaveRegions.add(new PointWiseComplementConvexHull(dim, point, convex));
                }
            }
        }
    }

    /**
     * Check whether a point is in the uncertain region and update labels for those of other partitions
     * @param sample point to be checked
     * @param flag true if pos region is convex, false otherwise
     * @return true if the point is in the uncertain region, false otherwise
     */
    public boolean isUsefulSample(DataPoint sample, boolean flag) {
        if (isInConvexRegion(sample,flag)) {
            return false;
        }
        return !isInConcaveRegion(sample, flag);
    }

    /**
     * Check whether a point is in the positive region
     * @param sample point to be checked
     * @param flag true if pos region is convex, false otherwise
     * @return true if the point is in the positive region, false otherwise
     */
    public boolean isInConvexRegion (DataPoint sample, boolean flag) {
        if(dim ==1){
            return flag ? oneDimTSM.isInConvexSeg(sample.get(0)) : oneDimTSM.isInConcaveRay(sample.get(0));
        }else {
            double[] point = sample.getData().toArray();
            return flag ? isInConvexRegion(point) : isInConcaveRegion(point);
        }
    }

    private boolean isInConvexRegion(double[] sample) {
        return convexRegion != null && convexRegion.containsPoint(sample);
    }

    /**
     * Check whether a point is in the negative region
     * @param sample point to be checked
     * @param flag true if pos region is convex, false otherwise
     * @return true if the point is in the negative region, false otherwise
     */
    public boolean isInConcaveRegion(DataPoint sample, boolean flag) {
        if(dim == 1){
            return flag ? oneDimTSM.isInConcaveRay(sample.get(0)) : oneDimTSM.isInConvexSeg(sample.get(0));
        } else {
            double[] point = sample.getData().toArray();
            return flag ? isInConcaveRegion(point) : isInConvexRegion(point);
        }
    }

    private boolean isInConcaveRegion(double[] sample) {
        return concaveRegions.stream().anyMatch(x -> x.containsPoint(sample));
    }

    /**
     * Find the duplicates in a two-dimension array
     * @param rawArray a two-dimension array
     * @param newArray an array to be checked
     * @return true if the array contains the same value as some elements of the given two-dimension array
     */
    public static boolean findDuplicates(double[][] rawArray, double[] newArray){
        return Arrays.stream(rawArray).anyMatch(x -> Arrays.equals(x, newArray));
    }

    /**
     * Find the duplicates in a hash set
     * @param dSets a hash set of arrays
     * @param point an array to be checked
     * @return true if the array contains the same value as some elements of the given hash set
     */
    // find duplicates in later iteration
    public static boolean findDuplicates(HashSet<double[]> dSets, double[] point){
        return dSets.stream().anyMatch(x -> Arrays.equals(x,point));
    }

    /**
     * @param convexhull
     * @param convexCones
     * @return  the set of the vertices of convex polytope and convex cones
     */
    public HashSet<double[]> getVertices(ConvexPolytope convexhull, ArrayList<PointWiseComplementConvexHull> convexCones){
        HashSet<double[]> vertices = new HashSet<>();

        getConvexHullVertices(convexhull, vertices);
        getConvexConesVertices(convexCones, vertices);

        return vertices;
    }

    /**
     * Add the vertices of convex cones to set of vertices of TSM
     * @param convexCones
     * @param vertices set of vertices of TSM
     */
    public void getConvexConesVertices(ArrayList<PointWiseComplementConvexHull> convexCones, HashSet<double[]> vertices) {
        if(convexCones!=null){
            for(PointWiseComplementConvexHull convexCone: convexCones){
                for(Facet facet: convexCone.getFacets()){
                    for(Vertex vertex: facet.getVertices()){
                        vertices.add(vertex.getValues());
                    }
                }
            }
        }
    }

    /**
     * Add the vertices of convex polytope to set of vertices of TSM
     * @param convexhull
     * @param vertices set of vertices of TSM
     */
    public void getConvexHullVertices(ConvexPolytope convexhull, HashSet<double[]> vertices){
        if(convexhull!=null){
            for(Facet facet : convexhull.getFacets()){
                for(Vertex vertex: facet.getVertices()){
                    vertices.add(vertex.getValues());
                }
            }
        }
    }

    /**
     * @return display the construction of convex region
     */
    public String convexPolytopeToString() {
        if (dim > 1) {
            return convexRegion != null ? "The concave polytope is: \n" + convexRegion.toString() : "No convex polytope";
        } else {
            return oneDimTSM.toString();
        }
    }

    /**
     * @return display the construction of concave region
     */
    public String convexConesToString(){
        if(dim > 1){
            return concaveRegions != null? "The convex cones are: \n" + concaveRegions.toString(): "No convex cones";

        } else {
            return oneDimTSM.toString();
        }
    }

    /**
     * @return the construction of convex region
     */
    public ConvexPolytope getConvexRegion(){ return convexRegion;}

    /**
     * @return the construction of concave region
     */
    public ArrayList<PointWiseComplementConvexHull> getConcaveRegion(){ return  concaveRegions;}

    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(dim > 1){
            sb.append(convexPolytopeToString()).append("\n");
            sb.append(convexConesToString()).append("\n");
        } else{
            return oneDimTSM.toString();
        }
        return sb.toString();
    }

}

