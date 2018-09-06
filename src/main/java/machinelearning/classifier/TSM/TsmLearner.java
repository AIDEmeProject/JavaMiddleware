package machinelearning.classifier.TSM;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;

import java.util.*;


/**
 * This class create a TSM partition on a subspace spanned by a subset of features
 *
 * @author lppeng, enhui
 */

public class TsmLearner {

    /**
     * Reservoir of initial pos points
     */
    private double[][] pos;

    /**
     * Count of pos points
     */
    private int posCount;

    /**
     * Reservoir of initial neg points
     */
    private ArrayList<double[]> neg;

    /**
     * True if the pos region has been initialized, false otherwise
     */
    private boolean posInitialized = false;

    /**
     * True if the neg region has been initialized, false otherwise
     */
    private boolean negInitialized = false;

    /**
     * Dim of vertices
     */
    private final int dim;

    /**
     * Convex polytope
     */
    private ConvexPolytope positiveRegion;

    /**
     * The union of convex cones
     */
    private final ArrayList<PointWiseComplementConvexHull> negativeRegions;

    /**
     * Points for evaluation
     */
    private final Collection<DataPoint> dataPoints;

    /**
     * Positive samples recognized by TSM
     */
    private final HashSet<Long> positiveSamples;

    /**
     * Negative samples recognized by TSM
     */
    private final HashSet<Long> negativeSamples;

    /**
     * Points remaining unknown to TSM
     */
    private HashSet<Long> uncertainSamples;

    // record vertices to remove duplicates
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
     * @param dataPoints points for evaluation
     */
    public TsmLearner(int dim, Collection<DataPoint> dataPoints) {
        this.dim = dim;

        this.dataPoints = dataPoints;
        positiveSamples = new HashSet<>();
        negativeSamples = new HashSet<>();
        uncertainSamples = LabeledDataset.getIdSet(dataPoints);

        pos = new double[dim + 1][];
        posCount = 0;
        neg = new ArrayList<>();
        negativeRegions = new ArrayList<>();
        oneDimTSM = new OneDimTSM();
    }

    /**
     * Rejection sampling of a positive sample from unlabeled data
     * @param labeledSampleIds ids of labeled points
     * @return id of a positive point that has not been labeled
     */
    public Long getRandomPositiveIdNotIn(HashSet<Long> labeledSampleIds) {
        for (Long posSampleId : positiveSamples) {
            if (!labeledSampleIds.contains(posSampleId)) {
                return posSampleId;
            }
        }
        return null;
    }

    /**
     * Rejection sampling of a negative sample from unlabeled data
     * @param labeledSampleIds ids of labeled points
     * @return id of a negative point that has not been labeled
     */
    public Long getRandomNegativeIdNotIn(HashSet<Long> labeledSampleIds) {
        for (Long negSampleId : negativeSamples) {
            if (!labeledSampleIds.contains(negSampleId)) {
                return negSampleId;
            }
        }
        return null;
    }

    /**
     * @return Ids of positive examples
     */
    public HashSet<Long> getPositiveSamplesIds() {
        return positiveSamples;
    }

    /**
     * @return Ids of negative examples
     */
    public HashSet<Long> getNegativeSamplesIds() {
        return negativeSamples;
    }

    /**
     * @return Ids of uncertain examples
     */
    public HashSet<Long> getUncertainSamplesIds() {
        return uncertainSamples;
    }

    /**
     * @return collection of data points for TSM
     */
    public Collection<DataPoint> getEpFromTSM(){
        return dataPoints;
    }

//    public DataPoint getEvaluatingTupleById(Long id) {
//        //Todo: find how to retrieve a point by its id
//        return null;
//    }

    /**
     * Update Three-Set Partition for numerical attributes when the positive region is convex
     * @param labeledSamples labeled point projected onto a subspace
     * @throws IllegalArgumentException if a negative point is found in positive region and vice versa
     */
    public void updatePosRatio(Collection<LabeledPoint> labeledSamples) {
        for (LabeledPoint t : labeledSamples) {
            double[] point = t.getData();
            if(dim==1) {
                oneDimTSM.updatePos(point[0], t.getLabel().asSign());
            }else {
                // check the label on a subspace
                if (t.getLabel().asSign() > 0) {
                    // check whether a positive point(including the boundary) is inside the concave region or not
                    if(negativeRegions!=null && negativeRegions.contains(point)){
                        throw new IllegalArgumentException("a positive point cannot be inside the concave(negative) region: " + Arrays.toString(point));
                    }
                    updateConvexRegion(point);
                } else {
                    // check whether a negative point (including the boundary) is inside the convex region or not
                    if(positiveRegion!=null && positiveRegion.containsPoint(point)){
                        throw new IllegalArgumentException("a negative point cannot be inside the convex(positive) region: " + Arrays.toString(point));
                    }
                    updateConcaveRegion(point);
                }

                // get the vertices of both pos and neg regions
                vertices = getVertices(positiveRegion, negativeRegions);
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
            double[] point = t.getData();
            if(dim==1) {
                oneDimTSM.updateNeg(point[0], t.getLabel().asSign());
            }else {
                if (t.getLabel().asSign() < 0) {
                    // check whether a negative point(including the boundary) is inside the concave region or not
                    if(negativeRegions!=null && negativeRegions.contains(point)){
                        throw new IllegalArgumentException("a negative point cannot be inside the concave(negative) region: " + Arrays.toString(point));
                    }
                    updateConvexRegion(point);
                } else {
                    // check whether a positive point( including the boundary) is inside the convex region or not
                    if(positiveRegion!=null && positiveRegion.containsPoint(point)){
                        throw new IllegalArgumentException("a positive point cannot be inside the convex(negative) region: " + Arrays.toString(point));
                    }

                    updateConcaveRegion(point);
                }

                // get the vertices of both pos and neg regions
                vertices = getVertices(positiveRegion, negativeRegions);
            }
        }
    }

    /**
     * Update the convex ploytope
     * @param point point to be used for TSM
     */
    private void updateConvexRegion(double[] point) {
        // posInitialized true means the convex hull has been created
        if (!posInitialized) {
            // remove the duplicates from pos points
            boolean isDuplicates = findDuplicates(pos, point);
            if (!isDuplicates) {
                pos[posCount++] = point;
            }

            // if this point is the (dim)-th positive sample and there already are some negative samples
            if (posCount == dim && !neg.isEmpty()) {
                // initialize the negative regions
                for (double[] negPoint : neg) {
                    negativeRegions.add(new PointWiseComplementConvexHull(dim, negPoint, pos));
                }
                negInitialized = true;
                neg.clear();
            } else if (posCount == dim + 1) {
                positiveRegion = new ConvexPolytope(dim, pos);
                posInitialized = true;
                if (negInitialized) {
                    for (PointWiseComplementConvexHull nr : negativeRegions) {
                        nr.addVertex(point);
                    }
                }
            }
        } else if (vertices != null && findDuplicates(vertices, point)) {
            //todo: check whether this step is necessary

        } else {
            positiveRegion.addVertex(point);
            for (PointWiseComplementConvexHull nr : negativeRegions) {
                nr.addVertex(point);
            }
        }
    }

    /**
     * Update the convex cones
     * @param point point to be used for TSM
     */
    private void updateConcaveRegion(double[] point) {
        // negInitialized true means the convex cone has been created
        if (!negInitialized) {
            if (posCount < dim) {
                // record the negative points until the convex polytope has been built
                neg.add(point);
            } else {
                // in this case, convex polytope has existed
                negativeRegions.add(new PointWiseComplementConvexHull(dim, point, pos));
                negInitialized = true;
            }
        } else {
            boolean createNew = true;
            for (PointWiseComplementConvexHull nr : negativeRegions) {
                if (nr.containsPoint(point)) {
                    // one of the convex cones contains this point
                    createNew = false;
                    break;
                }
            }
            if (createNew) {
                if (positiveRegion != null) {
                    negativeRegions.add(new PointWiseComplementConvexHull(dim, point, positiveRegion));
                } else {
                    // although no positive region exists, there must be a facet
                    negativeRegions.add(new PointWiseComplementConvexHull(dim, point, pos));
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
    public boolean isUsefulSample(LabeledPoint sample, boolean flag) {
        if (isInPositiveRegion(sample,flag)) {
            sample.setLabel(1);
            return false;
        }
        if (isInNegativeRegion(sample,flag)) {
            sample.setLabel(-1);
            return false;
        }
        return true;
    }

    /**
     * Check whether a point is in the positive region
     * @param sample point to be checked
     * @param flag true if pos region is convex, false otherwise
     * @return true if the point is in the positive region, false otherwise
     */
    public boolean isInPositiveRegion (LabeledPoint sample, boolean flag) {
        if(dim ==1){
            if(flag){
                ArrayList<Double> convexLineSeg = oneDimTSM.getConvexLineSeg();
                return ((convexLineSeg.size() == 2 && oneDimTSM.isInConvexSeg(sample.getData()[0])) || (convexLineSeg.size() > 0 && sample.getData()[0] == convexLineSeg.get(0)));
            } else {
                ArrayList<Double> concaveRay = oneDimTSM.getConcaveRay();
                return (concaveRay.size() == 2 && oneDimTSM.isInConcaveRay(sample.getData()[0]));
            }
        }else {
            if (flag) {
                return (positiveRegion != null && positiveRegion.containsPoint(sample.getData()));
            }else {
                for(PointWiseComplementConvexHull nr:negativeRegions) {
                    if(nr.containsPoint(sample.getData())) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * Check whether a point is in the negative region
     * @param sample point to be checked
     * @param flag true if pos region is convex, false otherwise
     * @return true if the point is in the negative region, false otherwise
     */
    public boolean isInNegativeRegion (LabeledPoint sample, boolean flag) {
        if(dim == 1){
            if(flag){
                ArrayList<Double> concaveRay = oneDimTSM.getConcaveRay();
                return (concaveRay.size() == 2 && oneDimTSM.isInConcaveRay(sample.getData()[0]));
            } else{
                ArrayList<Double> convexLineSeg = oneDimTSM.getConvexLineSeg();
                return ((convexLineSeg.size() == 2 && oneDimTSM.isInConvexSeg(sample.getData()[0])) || (convexLineSeg.size() > 0 && sample.getData()[0] == convexLineSeg.get(0)));
            }
        }else {

            if (flag) {
                for(PointWiseComplementConvexHull nr:negativeRegions) {
                    if(nr.containsPoint(sample.getData())) {
                        return true;
                    }
                }
                return false;
            }else{
                return (positiveRegion != null && positiveRegion.containsPoint(sample.getData()));
            }
        }
    }

    /**
     * Find the duplicates in a two-dimension array
     * @param rawArray a two-dimension array
     * @param newArray an array to be checked
     * @return true if the array contains the same value as some elements of the given two-dimension array
     */
    public static boolean findDuplicates(double[][] rawArray, double[] newArray){
        for(double[] item : rawArray){
            if(Arrays.equals(item, newArray)){
                return true;
            }
        }
        return false;
    }

    /**
     * Find the duplicates in a hash set
     * @param dSets a hash set of arrays
     * @param point an array to be checked
     * @return true if the array contains the same value as some elements of the given hash set
     */
    // find duplicates in later iteration
    public static boolean findDuplicates(HashSet<double[]> dSets, double[] point){
        for(double[] item: dSets){
            if(Arrays.equals(item, point)){
                return true;
            }
        }
        return false;
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
    private void getConvexConesVertices(ArrayList<PointWiseComplementConvexHull> convexCones, HashSet<double[]> vertices) {
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
    private void getConvexHullVertices(ConvexPolytope convexhull, HashSet<double[]> vertices){
        if(convexhull!=null){
            for(Facet facet : convexhull.getFacets()){
                for(Vertex vertex: facet.getVertices()){
                    vertices.add(vertex.getValues());
                }
            }
        }
    }

}

