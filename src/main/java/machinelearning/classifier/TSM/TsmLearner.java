//package machinelearning.classifier.TSM;
//
//import data.DataPoint;
//import data.LabeledDataset;
//import data.LabeledPoint;
//
//import java.io.IOException;
//import java.util.*;
//
//
///**
// * This class create a TSM partition on a subspace spanned by a subset of features
// *
// * @author lppeng, enhui
// */
//
//public class TsmLearner {
//
//    // for initialization
//    /**
//     * Reservoir of initial pos points
//     */
//    private double[][] pos;
//
//    /**
//     * Count of pos points
//     */
//    private int posCount;
//
//    /**
//     * Reservoir of initial neg points
//     */
//    private ArrayList<double[]> neg;
//
//    /**
//     * True if the pos region has been initialized, otherwise false
//     */
//    private boolean posInitialized = false;
//
//    /**
//     * True if the neg region has been initialized, otherwise false
//     */
//    private boolean negInitialized = false;
//
//
//    // multi-dimensional
//    /**
//     * Dim of vertices
//     */
//    private final int dim;
//
//    /**
//     * Convex polytope
//     */
//    private ConvexPolytope positiveRegion;
//
//    /**
//     * The union of convex cones
//     */
//    private final ArrayList<PointWiseComplementConvexHull> negativeRegions;
//
//    // for evaluation
//    /**
//     * Points for evaluation
//     */
//    private final Collection<DataPoint> ep;
//
//    /**
//     * Positive samples recognized by TSM
//     */
//    private final HashSet<Long> positiveSamples;
//
//    /**
//     * Negative samples recognized by TSM
//     */
//    private final HashSet<Long> negativeSamples;
//
//    /**
//     * Points remaining unknown to TSM
//     */
//    private HashSet<Long> uncertainSamples;
//
//    /**
//     * TSM value
//     */
//    private double value;
//
//    // record vertices to avoid duplicates
//    /**
//     * Vertices of the convex polytope and convex cone
//     */
//    private HashSet<double[]> vertices;
//
//    // one dimension TSM
//    /**
//     * Three-set partition on 1-dim space
//     */
//    private OneDimTSM oneDimTSM;
//
//    // tsm for one-hot encoding categorical variables
//    // restore truth and false values
//    //todo: replace this by a new class which addresses categorical attributes
//    private HashSet<Integer> truthLines;
//    private HashSet<Integer> falseLines;
//
//
//    /**
//     * Create Three-Set Partition for any dimensional space
//     * @param dim dim of vertices
//     * @param ep points for evaluation
//     */
//    public TsmLearner(int dim, Collection<DataPoint> ep) {
//        this.dim = dim;
//
//        this.ep = ep;
//        positiveSamples = new HashSet<>();
//        negativeSamples = new HashSet<>();
//        uncertainSamples = LabeledDataset.getIdSet(ep);
//
//        pos = new double[dim + 1][];
//        posCount = 0;
//        neg = new ArrayList<>();
//        negativeRegions = new ArrayList<>();
//        oneDimTSM = new OneDimTSM();
//
//        truthLines = new HashSet<>();
//        falseLines = new HashSet<>();
//    }
//
//    /**
//     * @param labeledSampleIds ids of labeled points
//     * @return id of a positive point that has not been labeled
//     */
//    public Long getRandomPositiveIdNotIn(HashSet<Long> labeledSampleIds) {
//        for (Long posSampleId : positiveSamples) {
//            if (!labeledSampleIds.contains(posSampleId)) {
//                return posSampleId;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * @param labeledSampleIds ids of labeled points
//     * @return id of a negative point that has not been labeled
//     */
//    public Long getRandomNegativeIdNotIn(HashSet<Long> labeledSampleIds) {
//        for (Long negSampleId : negativeSamples) {
//            if (!labeledSampleIds.contains(negSampleId)) {
//                return negSampleId;
//            }
//        }
//        return null;
//    }
//
//
//    public DataPoint getEvaluatingTupleById(Long id) {
//        //Todo: find how to retrieve a point by its id
//        return null;
//    }
//
//
//    public void updateRatio(Collection<LabeledPoint> labeledSamples) throws IOException {
//        for (LabeledPoint t : labeledSamples) {
//            double[] point = t.getSelectedScaledArray();
//            if(dim==1){
//                if(t.tsmLabel > 0){
//                    // check whether the sample is in concave region
//                    if(concaveRay.size()==2 && isInConcaveRay(point[0])){
//                        throw new IOException("A positive point is in negative rays");
//                    }
//                    if(!convexInitialized){
//                        convexLineSeg.add(0, point[0]);
//                        convexInitialized = true;
//                    }else if(convexLineSeg.size()==1){
//                        if(point[0] > convexLineSeg.get(0)){
//                            convexLineSeg.add(1, point[0]);
//                        }else if(point[0] < convexLineSeg.get(0)) {
//                            convexLineSeg.add(1, convexLineSeg.get(0));
//                            convexLineSeg.set(0, point[0]);
//                        }
//                    }else if(convexLineSeg.size() == 2){
//                        if(point[0] > convexLineSeg.get(1)){
//                            convexLineSeg.set(1, point[0]);
//                        }else if(point[0] < convexLineSeg.get(0)) {
//                            convexLineSeg.set(0, point[0]);
//                        }
//                    }else {
//                        throw new IOException("The end points of  convex region should be 2 instead of " + convexLineSeg.size() + ": " + Arrays.toString(convexLineSeg.toArray()));
//                    }
//                } else if((convexLineSeg.size()==2 && isInConvexSeg(point[0])) || (convexLineSeg.size()==1 && point[0] == convexLineSeg.get(0))){
//                    throw new IOException("A negative point is in positive convex interval");
//                } else if (!convexInitialized) {
//                    concavePoints.add(point[0]);
//                } else {
//                    if (!concaveInitialized) {
//                        // assuming a convex end point exists before adding concave end points. todo: what if the concave end points are added ahead of convex end points
//                        if (concavePoints.size() > 0) {
//                            concavePoints.add(point[0]);
//                            // find two points in concave region which are closest to the convex region
//                            double[] subConcaveRays = findTopK(concavePoints, 2, convexLineSeg.get(0));
//                            if (subConcaveRays[0] > convexLineSeg.get(0)) {
//                                concaveRay.add(0, Double.NEGATIVE_INFINITY);
//                                concaveRay.add(1, subConcaveRays[0]);
//                            } else if (subConcaveRays[1] < convexLineSeg.get(0)) {
//                                concaveRay.add(0, subConcaveRays[1]);
//                                concaveRay.add(1, Double.POSITIVE_INFINITY);
//                            } else {
//                                concaveRay.add(0, subConcaveRays[0]);
//                                concaveRay.add(1, subConcaveRays[1]);
//                            }
//                        }if(point[0] > convexLineSeg.get(0)){
//                            concaveRay.add(0, Double.NEGATIVE_INFINITY);
//                            concaveRay.add(1, point[0]);
//                        }else if(point[0] < convexLineSeg.get(0)) {
//                            concaveRay.add(0, point[0]);
//                            concaveRay.add(1, Double.POSITIVE_INFINITY);
//                        }else {
//                            throw new IOException("A point " + point[0] + " in the convex line segment also lies in concave rays.");
//                        }
//                        concaveInitialized = true;
//                    }else {
//                        if(convexLineSeg.size() == 1){
//                            if(point[0] < concaveRay.get(1) && point[0] > convexLineSeg.get(0)){
//                                concaveRay.set(1, point[0]);
//                            }else if(point[0] > concaveRay.get(0) && point[0] < convexLineSeg.get(0)){
//                                concaveRay.set(0, point[0]);
//                            }
//                        }else if(convexLineSeg.size() == 2){
//                            if(point[0] < concaveRay.get(1) && point[0] > convexLineSeg.get(1)){
//                                concaveRay.set(1, point[0]);
//                            }else if(point[0] > concaveRay.get(0) && point[0] < convexLineSeg.get(0)){
//                                concaveRay.set(0, point[0]);
//                            }
//                        }else {
//                            throw new IOException("The end points of convex region should be 2 instead of " + convexLineSeg.size() + ": " + Arrays.toString(convexLineSeg.toArray()));
//                        }
//                    }
//                }
//                if(Configuration.isDebug()){
//                    if(convexLineSeg.size() ==2){
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " convex region " + convexLineSeg.get(0) + " " + convexLineSeg.get(1));
//                    }else {
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " convex region  " + convexLineSeg.get(0));
//                    }
//                    if(concaveRay.size() ==2){
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " concave region " + concaveRay.get(0) + " " + concaveRay.get(1));
//                    }else if(concaveRay.size() == 1){
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " concave region " + concaveRay.get(0));
//                    }
//                }
//
//            }else {
//                // tsmLabel represents the label for subquery
//                if (t.tsmLabel > 0) {
//                    // posInitialized true means the convex hull has been created
//                    if (!posInitialized) {
//                        // remove the duplicates in pos points
//                        boolean isDuplicates = findDuplicates(pos, point);
//                        if (!isDuplicates) {
//                            pos[posCount++] = point;
//                        }
//
//                        // if this point is the (dim)-th positive sample and there already are some negative samples
//                        if (posCount == dim && !neg.isEmpty()) {
//                            // initialize the negative regions
//                            for (double[] negPoint : neg) {
//                                negativeRegions.add(new PointWiseComplementConvexHull(dim, negPoint, pos));
//                            }
//                            negInitialized = true;
//                            neg.clear();
//                        } else if (posCount == dim + 1) {
//                            positiveRegion = new ConvexPolytope(dim, pos);
//                            posInitialized = true;
//                            if (negInitialized) {
//                                for (PointWiseComplementConvexHull nr : negativeRegions) {
//                                    nr.addVertex(point);
//                                }
//                            }
//                        }
//                    } else if (vertices != null && findDuplicates(vertices, point)) {
//
//                    } else {
//                        positiveRegion.addVertex(point);
//                        for (PointWiseComplementConvexHull nr : negativeRegions) {
//                            nr.addVertex(point);
//                        }
//                    }
//                } else {
//                    // Check whether a negative point (including the boundary) is inside the convex region or not
//                    if(positiveRegion!=null && positiveRegion.containsPoint(point)){
//                        throw new IOException("a negative point cannot be inside the convex(positive) region: " + Arrays.toString(point));
//                    }
//
//                    if (!negInitialized) {
//                        if (posCount < dim) {
//                            neg.add(point);
//                        } else {
//                            negativeRegions.add(new PointWiseComplementConvexHull(dim, point, pos));
//                            negInitialized = true;
//                        }
//                    } else {
//                        boolean createNew = true;
//                        for (PointWiseComplementConvexHull nr : negativeRegions) {
//                            if (nr.containsPoint(point)) {
//                                createNew = false;
//                                break;
//                            }
//                        }
//                        if (createNew) {
//                            if (positiveRegion != null) {
//                                negativeRegions.add(new PointWiseComplementConvexHull(dim, point, positiveRegion));
//                            } else {
//                                negativeRegions.add(new PointWiseComplementConvexHull(dim, point, pos));
//                            }
//                        }
//                    }
//                }
//
//
//                // calculate partial tsm value
//        /*
//        value = (double) positiveSamples.size() / (positiveSamples.size() + uncertainSamples.size());*/
//                vertices = getVertices(positiveRegion, negativeRegions);
//            }
//        }
//
//    }
//
//    public boolean isUsefulSample(Tuple sample) throws IOException {
//        if (isInPositiveRegion(sample)) {
//            sample.setLabel(1d);
//            return false;
//        }
//        if (isInNegativeRegion(sample)) {
//            sample.setLabel(-1d);
//            return false;
//        }
//        return true;
//    }
//
//    public boolean isInPositiveRegion(Tuple sample) throws IOException {
//        return (positiveRegion != null && positiveRegion.containsPoint(sample.getSelectedScaledArray()));
//    }
//
//    public boolean isInNegativeRegion(Tuple sample) throws IOException {
//        for (PointWiseComplementConvexHull nr : negativeRegions) {
//            if (nr.containsPoint(sample.getSelectedScaledArray())) {
//                return true;
//            }
//        }
//        return false;
//    }
//    public HashSet<Long> getPositiveSamplesIds() {
//        return positiveSamples;
//    }
//
//    public HashSet<Long> getNegativeSamplesIds() {
//        return negativeSamples;
//    }
//
//    public HashSet<Long> getUncertainSamplesIds() {
//        return uncertainSamples;
//    }
//
//    public Collection<DataPoint> getEpFromTSM(){
//        return ep;
//    }
//
//
//    // TSM on negative region
//    // flag: true means pos region is convex, otherwise neg region is convex
//    public void updateRatioNeg(Collection<Tuple> labeledSamples) throws IOException {
//        // todo: consider the case when positive point has been added ahead of negative point
//        for(Tuple t: labeledSamples) {
//
//            double[] point = t.getSelectedScaledArray();
////			System.out.println("point" + Arrays.toString(point));
//            //Reverse the role of pos and neg
//            if(dim==1) {
//                if (t.tsmLabel < 0) {
//                    // check whether the sample is in concave region
//                    if(concaveRay.size()==2 && isInConcaveRay(point[0])){
//                        throw new IOException("A negative point is in positive rays");
//                    }
//                    if (!convexInitialized) {
//                        convexLineSeg.add(0, point[0]);
//                        convexInitialized = true;
//                    } else if (convexLineSeg.size() == 1) {
//                        if (point[0] > convexLineSeg.get(0)) {
//                            convexLineSeg.add(1, point[0]);
//                        } else if (point[0] < convexLineSeg.get(0)) {
//                            convexLineSeg.add(1, convexLineSeg.get(0));
//                            convexLineSeg.set(0, point[0]);
//                        }
//                    } else if (convexLineSeg.size() == 2) {
//                        if (point[0] > convexLineSeg.get(1)) {
//                            convexLineSeg.set(1, point[0]);
//                        } else if (point[0] < convexLineSeg.get(0)) {
//                            convexLineSeg.set(0, point[0]);
//                        }
//                    } else {
//                        throw new IOException("The end points should be 2 instead of " + convexLineSeg.size() + ": " + Arrays.toString(convexLineSeg.toArray()));
//                    }
//                } else if((convexLineSeg.size()==2 && isInConvexSeg(point[0])) || (convexLineSeg.size()==1 && point[0] == convexLineSeg.get(0))){
//                    throw new IOException("A positive point is in negative convex interval");
//                } else if (!convexInitialized) {
//                    concavePoints.add(point[0]);
//                } else {
//                    if (!concaveInitialized) {
//                        // assuming a convex end point exists before adding concave end points. todo: what if the concave end points are added ahead of convex end points
//                        if (concavePoints.size() > 0) {
//                            concavePoints.add(point[0]);
//                            // find two points in concave region which are closest to the convex region
//                            double[] subConcaveRays = findTopK(concavePoints, 2, convexLineSeg.get(0));
//                            if (subConcaveRays[0] > convexLineSeg.get(0)) {
//                                concaveRay.add(0, Double.NEGATIVE_INFINITY);
//                                concaveRay.add(1, subConcaveRays[0]);
//                            } else if (subConcaveRays[1] < convexLineSeg.get(0)) {
//                                concaveRay.add(0, subConcaveRays[1]);
//                                concaveRay.add(1, Double.POSITIVE_INFINITY);
//                            } else {
//                                concaveRay.add(0, subConcaveRays[0]);
//                                concaveRay.add(1, subConcaveRays[1]);
//                            }
//
//                        } else if (point[0] > convexLineSeg.get(0)) {
//                            concaveRay.add(0, Double.NEGATIVE_INFINITY);
//                            concaveRay.add(1, point[0]);
//                        } else if (point[0] < convexLineSeg.get(0)) {
//                            concaveRay.add(0, point[0]);
//                            concaveRay.add(1, Double.POSITIVE_INFINITY);
//                        } else {
//                            throw new IOException("A point " + point[0] + " in the convex line segment also lies in concave rays.");
//                        }
//                        concaveInitialized = true;
//                    } else {
//                        if (convexLineSeg.size() == 1) {
//                            if (point[0] < concaveRay.get(1) && point[0] > convexLineSeg.get(0)) {
//                                concaveRay.set(1, point[0]);
//                            } else if (point[0] > concaveRay.get(0) && point[0] < convexLineSeg.get(0)) {
//                                concaveRay.set(0, point[0]);
//                            }
//                        } else if (convexLineSeg.size() == 2) {
//                            if (point[0] < concaveRay.get(1) && point[0] > convexLineSeg.get(1)) {
//                                concaveRay.set(1, point[0]);
//                            } else if (point[0] > concaveRay.get(0) && point[0] < convexLineSeg.get(0)) {
//                                concaveRay.set(0, point[0]);
//                            }
//                        } else {
//                            throw new IOException("The end points should be 2 instead of " + convexLineSeg.size() + ": " + Arrays.toString(convexLineSeg.toArray()));
//                        }
//                    }
//                }
//
//                if (Configuration.isDebug()) {
//                    if (convexLineSeg.size() == 2) {
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " convex region " + convexLineSeg.get(0) + " " + convexLineSeg.get(1));
//                    } else {
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " convex region  " + convexLineSeg.get(0));
//                    }
//                    if (concaveRay.size() == 2) {
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " concave region " + concaveRay.get(0) + " " + concaveRay.get(1));
//                    } else if (concaveRay.size() == 1) {
//                        System.out.println("for " + Arrays.toString(t.getSelectedScaledAV().toArray()) + " concave region " + concaveRay.get(0));
//                    }
//                }
//            }else {
//                if (t.tsmLabel < 0) {
//                    if (!posInitialized) {
//                        // check whether the point to be added is equal to previous ones or not
//                        boolean isDuplicates = findDuplicates(pos, point);
//                        if(!isDuplicates){
//                            pos[posCount++] = point;
//                        }
//
//                        // if this point is the (dim)-th positive sample and there already are some negative samples
//                        if (posCount == dim && !neg.isEmpty()) {
//                            // initialize the negative regions
//                            for (double[] negPoint : neg) {
//                                negativeRegions.add(new PointWiseComplementConvexHull(dim, negPoint, pos));
//                            }
//                            negInitialized = true;
//                            neg.clear();
//                        } else if (posCount == dim + 1) {
//                            positiveRegion = new ConvexPolytope(dim, pos);
//                            posInitialized = true;
//                            if (negInitialized) {
//                                for (PointWiseComplementConvexHull nr : negativeRegions) {
//                                    nr.addVertex(point);
//                                }
//                            }
//                        }
//                    } else if(vertices!=null && findDuplicates(vertices,point)){
//
//                    }else {
//                        positiveRegion.addVertex(point);
//                        for (PointWiseComplementConvexHull nr : negativeRegions) {
//                            nr.addVertex(point);
//                        }
//                    }
//                } else {
//                    // check whether a positive point( including those on the boundary) is inside the convex region or not
//                    if(positiveRegion!=null && positiveRegion.containsPoint(point)){
//                        throw new IOException("a positive point cannot be inside the convex(negative) region: " + Arrays.toString(point));
//                    }
//
//                    if (!negInitialized) {
//                        if (posCount < dim) {
//                            neg.add(point);
//                        } else {
//                            negativeRegions.add(new PointWiseComplementConvexHull(dim, point, pos));
//                            negInitialized = true;
//                        }
//                    } else {
//                        boolean createNew = true;
//                        for (PointWiseComplementConvexHull nr : negativeRegions) {
//                            if (nr.containsPoint(point)) {
//                                createNew = false;
//                                break;
//                            }
//                        }
//                        if (createNew) {
//                            if (positiveRegion != null) {
//                                negativeRegions.add(new PointWiseComplementConvexHull(dim, point, positiveRegion));
//                            } else {
//                                negativeRegions.add(new PointWiseComplementConvexHull(dim, point, pos));
//                            }
//                        }
//                    }
//                }
//
//
//
//                // calculate partial TSM value
//        /*for (Iterator<Long> i = uncertainSamples.iterator(); i.hasNext();) {
//            Long key = i.next();
//            double [] value = ep.points.get(key).getSelectedScaledArray();
//            if (positiveRegion!=null && positiveRegion.containsPoint(value)) {
//                i.remove();
//                negativeSamples.add(key);
//            }
//            else {
//                for(PointWiseComplementConvexHull nr:negativeRegions) {
//                    if(nr.containsPoint(value)) {
//                        i.remove();
//                        positiveSamples.add(key);
//                        break;
//                    }
//                }
//            }
//        }
//        value = (double)positiveSamples.size()/(positiveSamples.size()+uncertainSamples.size());*/
//                vertices = getVertices(positiveRegion, negativeRegions);
//            }
//        }
//    }
//
//    // two ways of creating TSM
//    public boolean isUsefulSample(Tuple sample, boolean flag) throws IOException {
//        if (isInPositiveRegion(sample,flag)) {
//            sample.setLabel(1d);
//            return false;
//        }
//        if (isInNegativeRegion(sample,flag)) {
//            sample.setLabel(-1d);
//            return false;
//        }
//        return true;
//    }
//
//    public boolean isInPositiveRegion (Tuple sample, boolean flag) throws IOException {
//        if(dim ==1){
//            if(flag){
//                return ((convexLineSeg.size() == 2 && isInConvexSeg(sample.getSelectedScaledArray()[0])) || (convexLineSeg.size() > 0 && sample.getSelectedScaledArray()[0] == convexLineSeg.get(0)));
//            } else {
//                return (concaveRay.size() == 2 && isInConcaveRay(sample.getSelectedScaledArray()[0]));
//            }
//        }else {
//            if (flag) {
//                return (positiveRegion != null && positiveRegion.containsPoint(sample.getSelectedScaledArray()));
//            }else {
//                for(PointWiseComplementConvexHull nr:negativeRegions) {
//                    if(nr.containsPoint(sample.getSelectedScaledArray())) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//
//        }
//    }
//
//    public boolean isInNegativeRegion (Tuple sample, boolean flag) throws IOException {
//        if(dim == 1){
//            if(flag){
//                return (concaveRay.size() == 2 && isInConcaveRay(sample.getSelectedScaledArray()[0]));
//            } else{
//                return ((convexLineSeg.size() == 2 && isInConvexSeg(sample.getSelectedScaledArray()[0])) || (convexLineSeg.size() > 0 && sample.getSelectedScaledArray()[0] == convexLineSeg.get(0)));
//            }
//        }else {
//
//            if (flag) {
//                for(PointWiseComplementConvexHull nr:negativeRegions) {
//                    if(nr.containsPoint(sample.getSelectedScaledArray())) {
//                        return true;
//                    }
//                }
//                return false;
//            }else{
//                return (positiveRegion != null && positiveRegion.containsPoint(sample.getSelectedScaledArray()));
//            }
//        }
//    }
//
//    //in case that in subspace, the beginning points of TSM cause errors
//    public static boolean findDuplicates(double[][] rawArray, double[] newArray){
//        for(double[] item : rawArray){
//            if(Arrays.equals(item, newArray)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // find duplicates in later iteration
//    public static boolean findDuplicates(HashSet<double[]> dSets, double[] point){
//        for(double[] item: dSets){
//            if(Arrays.equals(item, point)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // return all the vertices of convex polytope and convex cones
//    public static HashSet<double[]> getVertices(ConvexPolytope convexhull, ArrayList<PointWiseComplementConvexHull> convexCones){
//        HashSet<double[]> vertices = new HashSet<>();
//        if(convexhull!=null){
//            for(Facet facet : convexhull.getFacets()){
//                for(Vertex vertex: facet.getVertices()){
//                    vertices.add(vertex.getValues());
//                }
//            }
//        }
//        if(convexCones!=null){
//            for(PointWiseComplementConvexHull convexCone: convexCones){
//                for(Facet facet: convexCone.getFacets()){
//                    for(Vertex vertex: facet.getVertices()){
//                        vertices.add(vertex.getValues());
//                    }
//                }
//            }
//        }
//
//        return vertices;
//    }
//
//
//    public static HashSet<double[]> getConvexhullVertices(ConvexPolytope convexhull){
//        HashSet<double[]> vertices = new HashSet<>();
//        if(convexhull!=null){
//            for(Facet facet : convexhull.getFacets()){
//                for(Vertex vertex: facet.getVertices()){
//                    vertices.add(vertex.getValues());
//                }
//            }
//        }
//        return vertices;
//    }
//
//    public static HashSet<double[]> getConvexconesVertices(ArrayList<PointWiseComplementConvexHull> convexCones){
//        HashSet<double[]> vertices = new HashSet<>();
//        if(convexCones!=null){
//            for(PointWiseComplementConvexHull convexCone: convexCones){
//                for(Facet facet: convexCone.getFacets()){
//                    for(Vertex vertex: facet.getVertices()){
//                        vertices.add(vertex.getValues());
//                    }
//                }
//            }
//        }
//        return vertices;
//    }
//
//
//
//
//    // one dimensional TSM
//    public boolean isInConvexSeg(double sample){
//        if(sample >= convexLineSeg.get(0) && sample <= convexLineSeg.get(1)){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean isInConcaveRay(double sample){
//        if(concaveRay.size()==2 && (sample <= concaveRay.get(0) || sample >= concaveRay.get(1))){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//
//    // tsm for categorical variables in the form of one-hot encoding
//    public void updateCatRatio(Collection<Tuple> labeledSamples) throws IOException {
//        for(Tuple t: labeledSamples) {
//            Collection <AttributeValue> rawValues = t.getSelectedUnscaledAV();
//            for(AttributeValue attributeValue: rawValues){
//                if(t.tsmLabel>0 && attributeValue.value > 0 ){
//                    // check whether the true value is in false value list
//                    if(falseLines.contains(attributeValue.index)){
//                        throw new IOException("A false value of " + attributeValue.index + " cannot be true!");
//                    }
//                    truthLines.add(attributeValue.index);
//                }else if(t.tsmLabel<0 && attributeValue.value > 0){
//                    // check whether the false value is in true value list
//                    if(truthLines.contains(attributeValue.index)){
//                        throw new IOException("A true value of " + attributeValue.index + " cannot be false!");
//                    }
//                    falseLines.add(attributeValue.index);
//                }
//            }
//        }
////        System.out.println("truthLines contains: " + Arrays.toString(truthLines.toArray()));
////        System.out.println("falseLines contains: " + Arrays.toString(falseLines.toArray()));
//    }
//
//    public boolean isOnTruthLines(Tuple sample) throws IOException {
//        if(truthLines.size() == 0){
//            return false;
//        }
//
//        int count = 0;
//        Collection <AttributeValue> rawValues = sample.getSelectedUnscaledAV();
//        for(AttributeValue attributeValue: rawValues){
//            if(truthLines.contains(attributeValue.index) && attributeValue.value > 0){
//                count+=1;
//            }
//        }
//
//        if(count > 1 ){
//            throw new IOException("Two cases can't be true at the same time" + sample.getSelectedUnscaledAV());
//        }else if(count == 1){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//
//
//    // todo: check whether there is a way to define strictly the negative regions
//    public boolean isOnFalseLines(Tuple sample) throws IOException {
//        if(falseLines.size() == 0){
//            return false;
//        }
//
//        int count = 0;
//        Collection <AttributeValue> rawValues = sample.getSelectedUnscaledAV();
//        for(AttributeValue attributeValue: rawValues){
//            if(falseLines.contains(attributeValue.index) && attributeValue.value > 0){
//                count+=1;
//            }
//        }
//        if(count > 1 ){
//            throw new IOException("Two cases can't be true at the same time" + sample.getSelectedUnscaledAV());
//        }else if(count == 1){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//
//
//
//    /*public Tuple factorizeCatFeatures(Tuple labeledSample, ArrayList<Integer> select_set) throws Exception {
//        for (int i = 0; i < labeledSample.getUnscaledAV().length; i++) {
//            if (select_set.contains(i)) {
//                labeledSample.selectAttribute(i);
//            } else {
//                labeledSample.unselectAttribute(i);
//            }
//
//        }
//        //System.out.println("before modeling:"+ t.getSelectedScaledAV());
//        return labeledSample;
//    }*/
//
//
//}
//
