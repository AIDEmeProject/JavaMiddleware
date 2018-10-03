package machinelearning.threesetmetric.TSM;

import data.DataPoint;
import data.LabeledPoint;
import explore.user.UserLabel;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.ExtendedLabel;

import java.util.*;

/**
 * Given a partition of an attributes set, we create a TSM on each subspace spanned by attributes in a certain partition.
 * This class combines together all the TSMs and create an overall TSM for the original space spanned by all the attributes.
 *
 * @author enhui
 */

public class MultiTSMLearner implements ExtendedClassifier {
    /**
     * List of TSMs on each subspace
     */
    private final ArrayList<TsmLearner> tsmSet;

    /**
     * Partition of the indices of attributes
     */
    private final ArrayList<int[]> feaGroups;

//    /**
//     * The collection of points used for TSM evaluation
//     */
//    private final Collection<DataPoint> testPoints;

    /**
     * Indicator of the type of attributes and the shape of pos and neg regions
     * first element: true --> pos in convex; false --> pos in concave
     * second element: true --> categorical; false --> numerical
     */
    private final ArrayList<boolean[]> tsmFlags;

//    // Evaluation
//    /**
//     * Indices of positive examples
//     */
//    private HashSet<Long> positiveSamples;
//
//    /**
//     * Indices of negative examples
//     */
//    private HashSet<Long> negativeSamples;
//
//    /**
//     * Indices of uncertain examples
//     */
//    private HashSet<Long> uncertainSamples;

    /**
     * Test the correctness of TSMs construction. For each element:
     * 0: untested; 1: TSM is invalid and assigned to be null, release memory
     */
    private ArrayList<Integer> testStates;

    /**
     * The opposite way to create TSM -- build convex polytope for negative regions
     */
    private final ArrayList<TsmLearner> backupTsmSet;

    /**
     * Record the errors occurring in standard TSM
     */
    private final int[] errTSM;

    /**
     * Record the errors occurring in the backup TSM
     */
    private final int[] errBackTSM;

    /**
     * Check whether the current way of constructing TSM has been changed
     * 0:unchanged, 1: changed
     */
    private int[] isFlagChanged;

    /**
     * The limit of errors. Todo: can we convert it to user-defined variable?
     */
    private static int threshold = 1;



    /**
     * @param feaGroups partition of attributes represented by indices
//     * @param testPoints examples for TSM evaluation
     * @param tsmFlags a list of indicators that correspond to attributes partition
     */
    public MultiTSMLearner(ArrayList<int[]> feaGroups,  ArrayList<boolean[]> tsmFlags) {
        this.feaGroups = feaGroups;
//        this.testPoints = testPoints;
        this.tsmFlags = tsmFlags;

        tsmSet = new ArrayList<>();
        testStates = new ArrayList<>();
        backupTsmSet = new ArrayList<>();


        // the following three int arrays are used to record conflicting points for TSM and initialized to be zero array.
        errTSM = new int[feaGroups.size()];
        errBackTSM = new int[feaGroups.size()];
        isFlagChanged = new int[feaGroups.size()];

        for(int[] selected_set : feaGroups){
            // initialize each TSM on each subspace
           // TsmLearner tsm = new TsmLearner(selected_set.length, factorizeFeatures(testPoints, selected_set));
            TsmLearner tsm = new TsmLearner(selected_set.length);
            tsmSet.add(tsm);
            //for hypothesis test
            testStates.add(0);
            // backup tsm is constructed according to the asssumption that negative region is convex
            TsmLearner backupTsm = new TsmLearner(selected_set.length);
            backupTsmSet.add(backupTsm);
        }
        System.out.println( "the number of tsms: " + tsmSet.size());

//        positiveSamples = new HashSet<>();
//        negativeSamples = new HashSet<>();
//        // assign test data for TSM evaluation
//        uncertainSamples = TsmLearner.getIdSet(testPoints);
    }

//    /**
//     * @return ids of positive examples known by TSM
//     */
//    public HashSet<Long> getPositiveSamplesIds(){
//        return positiveSamples;
//    }
//
//    /**
//     * @return ids of negative examples known by TSM
//     */
//    public HashSet<Long> getNegativeSamplesIds() {
//        return negativeSamples;
//    }
//
//    /**
//     * @return ids of examples unknown for TSM
//     */
//    public HashSet<Long> getUncertainSamplesIds() { return uncertainSamples; }
//
//    /**
//     * Rejection sampling of a positive sample from unlabeled data
//     * @param labeledSampleIds ids of labeled points
//     * @return id of a positive point that has not been labeled
//     */
//    public Long getRandomPositiveIdNotIn (HashSet<Long> labeledSampleIds) {
//        for (Long posSampleId: positiveSamples) {
//            if (!labeledSampleIds.contains(posSampleId)) {
//                return posSampleId;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Rejection sampling of a negative sample from unlabeled data
//     * @param labeledSampleIds ids of labeled points
//     * @return id of a negative point that has not been labeled
//     */
//    public Long getRandomNegativeIdNotIn (HashSet<Long> labeledSampleIds) {
//        for (Long negSampleId: negativeSamples) {
//            if (!labeledSampleIds.contains(negSampleId)) {
//                return negSampleId;
//            }
//        }
//        return null;
//    }

    //Todo: retrive a point by the ids
//    public Tuple getEvaluatingTupleById (Long id) {
//        return ep.points.get(id);
//    }


    /**
     * Update TSM on each subspace and combine them together based on the conjunctive assumption
     * @param labeledSamples labeled examples for TSM construction
     */
    @Override
    public void update(Collection<LabeledPoint> labeledSamples) {
        for(int i=0;i < tsmSet.size();i++){
            //System.out.println("The index of tsm is: " + i  +" --- and feature group is: " + Arrays.toString(feaGroups.get(i)));
            if(tsmFlags.get(i)[1]){
                // if the second element of flags array is true, the corresponding attribute is a categorical attribute
                if(tsmSet.get(i)!=null){
                    try{
                        if(labeledSamples!=null){
                            tsmSet.get(i).updateCat(factorizeFeatures(labeledSamples, feaGroups.get(i), i));
                        }
                    }catch (IllegalArgumentException e){
                        errTSM[i]++;
                        System.out.println("-0-");
                        System.out.println(e.toString());
                    }
                    if(errTSM[i] >= threshold){
                        tsmSet.set(i, null);
                    }
                }
            } else if(testStates.get(i)==0){
                if(tsmSet.get(i)!=null){
                    try{
                        if(labeledSamples!=null) {
                            tsmSet.get(i).updatePosRatio(factorizeFeatures(labeledSamples, feaGroups.get(i), i));
                        }
                        //System.out.println("partition: " + feaGroups.get(i) + " with flag: " + tsmFlags.get(i));
                        //System.out.println(factorizeFeatures(labeledSamples, feaGroups.get(i), truthKeySet.get(i)).size());
                    }catch (Exception e){
                        errTSM[i]++;
//                        tsmSet.set(i, null);
                        System.out.println("-2-");
                        System.out.println(e.toString());
                    }
                }
                if(errTSM[i] >= threshold){
                    tsmSet.set(i, null);
                    //System.out.println("-1-");
                }

                if(backupTsmSet.get(i)!=null){
                    try{
                        if(labeledSamples!=null){
                            backupTsmSet.get(i).updateNegRatio(factorizeFeatures(labeledSamples, feaGroups.get(i), i));
                        }
                    }catch (Exception e){
                        errBackTSM[i]++;
                        System.out.println("-4-");
                        System.out.println(e.toString());
                    }
                }
                if(errBackTSM[i] >= threshold){
                    backupTsmSet.set(i, null);
                    //System.out.println("-3-");
                }

                boolean oldFlag = tsmFlags.get(i)[0];
                if(tsmSet.get(i)==null && backupTsmSet.get(i) == null){
                    testStates.set(i, 1);
                }else if(tsmSet.get(i)==null && backupTsmSet.get(i)!=null){
                    tsmFlags.get(i)[0]=false;
                }else if(tsmSet.get(i)!=null && backupTsmSet.get(i)==null){
                    tsmFlags.get(i)[0]=true;
                }else {
                    if(errTSM[i] <= errBackTSM[i]){
                        tsmFlags.get(i)[0]=true;
                    }else {
                        tsmFlags.get(i)[0]=false;
                    }
                }
                if(tsmFlags.get(i)[0]==oldFlag){
                    isFlagChanged[i] = 0;
                }else {
                    isFlagChanged[i] = 1;
                }


                // if threshold < min(err(tsmSet[i]), backupTsmSet[i]), neither convex+, nor convex-
                // if err(tsmSet[i]) < err(backupTsmSet[i]) and err(tsmSet[i]) < threshold, set tsmFlag to positive(convex+) and update the model
                // if err(backupTsmSet[i]) < err(tsmSet[i]) and err(backupTsmSet[i]) < threshold, set tsmFlag to negative(convex-) and update the model
            }
        }
//        System.out.println("Error of convex+: " + Arrays.toString(errTSM) + "Error of convex-: " + Arrays.toString(errBackTSM));
//        System.out.println( "tsm's state of hypothesis test: " + Arrays.toString(testStates.toArray()));
//        System.out.println("Tsm flag of convex assumption is: " + Arrays.deepToString(tsmFlags.toArray()));
    }


    /**
     * Prediction of a given point: positive, negative and unknown
     * @param point
     * @return three-class label of a given point
     */
    @Override
    public ExtendedLabel predict(DataPoint point) {
        if(isInConvexRegion(point)){
            return ExtendedLabel.POSITIVE;
        }else if(isInConcaveRegion(point)){
            return ExtendedLabel.NEGATIVE;
        }else {
            return ExtendedLabel.UNKNOWN;
        }
    }


    /**
     * Verify if an example is positive
     * @param sample an example to be checked
     * @return true if the example is in the positive region, false otherwise
     */
    public boolean isInConvexRegion (DataPoint sample) {
        ArrayList<Boolean> catTruth = new ArrayList<> ();
        for(int i=0; i < tsmSet.size(); i++){
            // for categorical variables, if the example is not on the truth lines, return false
            if(tsmFlags.get(i)[1]){
                if(tsmSet.get(i) != null){
                    catTruth.add(tsmSet.get(i).isOnTruthLines(sample));
                } else {
                    catTruth.add(false);
                }
            }else{
                // for numerical variables
                DataPoint newSample = factorizeFeatures(sample, feaGroups.get(i));
                boolean flag = tsmFlags.get(i)[0];
                if(flag){
                    if(tsmSet.get(i) == null || !tsmSet.get(i).isInConvexRegion(newSample, true)){
                        return false;
                    }
                }else {
                    if(backupTsmSet.get(i)==null || !backupTsmSet.get(i).isInConvexRegion(newSample, false)){
                        return false;
                    }
                }
            }
        }

        return (!catTruth.contains(false));
    }

    /**
     * Verify if an example is negative
     * @param sample an example to be checked
     * @return true if the example is in the negative region, false otherwise
     */
    public boolean isInConcaveRegion (DataPoint sample) {
        for(int i=0; i < tsmSet.size(); i++){
            if(tsmFlags.get(i)[1]){
                if(tsmSet.get(i) != null && tsmSet.get(i).isOnFalseLines(sample)){
                    return true;
                }
            }else {
                DataPoint newSample = factorizeFeatures(sample, feaGroups.get(i));
                boolean flag = tsmFlags.get(i)[0];
                if(flag){
                    if(tsmSet.get(i)!= null && tsmSet.get(i).isInConcaveRegion(newSample, true)) {
                        return true;
                    }
                }else {
                    //todo: remove tsmFlags from threeset metric
                    if(backupTsmSet.get(i)!= null && backupTsmSet.get(i).isInConcaveRegion(newSample, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

//    /**
//     * Verify if an example is neither positive nor negative
//     * @param sample an example to be checked
//     * @return true if the example is in the unknown region, false otherwise
//     */
//    public boolean isUncertainSample(DataPoint sample) {
//        if (isInConvexRegion(sample)) {
//            return false;
//        }
//        if (isInConcaveRegion(sample)) {
//            return false;
//        }
//        return true;
//    }

    /*
    Test whether all partitions have been null or not
     */
    public boolean isTSMsetNull(){
        return (!testStates.contains(0));
    }


    public Collection<LabeledPoint> factorizeFeatures(Collection<LabeledPoint> labeledSamples, int[] select_set, int index) {
        Collection<LabeledPoint> dataPointsCopy = new ArrayList<>();
        for(LabeledPoint dataPoint : labeledSamples){
            //System.out.println("The partialLabels: " + Arrays.toString(dataPoint.getLabel().getLabelsForEachSubspace()));
            UserLabel label = dataPoint.getLabel().getLabelsForEachSubspace()[index];
            LabeledPoint partialPoint = new LabeledPoint(new DataPoint(dataPoint.getId(),dataPoint.getSelectedAttributes(select_set)), label);
            //System.out.println("The size of factorized point: " + partialPoint.getDim() + " The size of original point: " + dataPoint.getDim());
            dataPointsCopy.add(partialPoint);
        }
        return dataPointsCopy;
    }


    /**
     * Project data onto a subspace
     * @param dataPoints data points to be evaluated
     * @param select_set indices of attributes in a certain partition
     * @return data projected onto a subspace
     */
    public Collection<DataPoint> factorizeFeatures(Collection<DataPoint> dataPoints, int[] select_set) {
        Collection<DataPoint> dataPointsCopy = new ArrayList<>();
        for(DataPoint dataPoint : dataPoints){
            DataPoint partialPoint = new DataPoint(dataPoint.getId(),dataPoint.getSelectedAttributes(select_set));
            dataPointsCopy.add(partialPoint);
        }
        return dataPointsCopy;
    }

    /**
     * Project an example onto a subspace
     * @param testSample point to be tested
     * @param select_set indices of attributes in a certain partition
     * @return point projected onto a subspace
     */
    public DataPoint factorizeFeatures(DataPoint testSample, int[] select_set) {
        return new DataPoint(testSample.getId(), testSample.getSelectedAttributes(select_set));
    }

//
//    /**
//     * Verify partial labels on a subspace for a given point
//     * @param truthKeys ids of positive examples on a subspace
//     * @param id
//     * @return POSITIVE if the example is in the truthkeys set, Negative otherwise.
//     */
//    public Label isTruthKeys(HashSet<Long> truthKeys, Long id){
//        return truthKeys.contains(id)? Label.POSITIVE : Label.NEGATIVE;
//    }


    //TODO: three-set metric will be calculated from the outside
//    public double getTsmValue() throws Exception {
//        boolean result = IntStream.of(isFlagChanged).anyMatch(x -> x==1);
//        // if the flag has been changed, check the correctness of three sets
//        if(result){
//            System.out.println("Three sets need double check!" + Arrays.deepToString(tsmFlags.toArray()));
//            positiveSamples = new HashSet<Long>();
//            negativeSamples = new HashSet<Long>();
//            uncertainSamples = new HashSet<Long>(ep.points.keySet());
////            if(positiveSamples.size() > 0){
////                for(Iterator<Long> posId = positiveSamples.iterator(); posId.hasNext();){
////                    Long key = posId.next();
////                    Tuple posSample = ep.points.get(key);
////                    if(!isInConvexRegion(posSample)){
////                        posId.remove();
////                        if(isInConcaveRegion(posSample)){
////                            negativeSamples.add(key);
////                        }else {
////                            uncertainSamples.add(key);
////                        }
////                    }
////                }
////            }
////            if(negativeSamples.size() > 0){
////                for(Iterator<Long> negId = negativeSamples.iterator(); negId.hasNext();){
////                    Long key = negId.next();
////                    Tuple negSample = ep.points.get(key);
////                    if(!isInConcaveRegion(negSample)){
////                        negId.remove();
////                        if(isInConvexRegion(negSample)){
////                            positiveSamples.add(key);
////                        }else {
////                            uncertainSamples.add(key);
////                        }
////                    }
////                }
////            }
//        }
//
//        if(uncertainSamples.size() > 0){
//            for (Iterator<Long> i = uncertainSamples.iterator(); i.hasNext();) {
//                Long key = i.next();
//                Tuple sample = ep.points.get(key);
//                if (isInConvexRegion(sample)) {
//                    i.remove();
//                    positiveSamples.add(key);
//                }else if (isInConcaveRegion(sample)){
//                    i.remove();
//                    negativeSamples.add(key);
//                }
//            }
//
//
//            value = (double)positiveSamples.size()/(positiveSamples.size()+uncertainSamples.size());
//        }
//        return value;
//    }




//    public HashSet<Long> getTrueKeys(ArrayList<Integer> select_set){
//        HashSet<Long> trueKeys = new HashSet<Long>();
//        Integer index = feaGroups.indexOf(select_set);
//        if(Configuration.isDebug()) {
//            System.out.println("loading ground truth....");
//        }
//
//        String sql = "select " + Configuration.getKeyAttribute() + " from " + Configuration.getTableName() + " where " + Configuration.getSubPredicates().get(index) + ";";
//
//        try {
//            ResultSet rs = Context.getStmt().executeQuery(sql);
//            while(rs.next()){
//                long key = rs.getLong(1);
//                trueKeys.add(key);
//            }
//            rs.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if(Configuration.isDebug()) {
//            System.out.println(sql);
//            System.out.println(trueKeys.size() + " results");
//        }
//
//        return trueKeys;
//    }

    public int tsmCount(){ return feaGroups.size();}


    public String toString(){
        StringBuilder sb = new StringBuilder();
        int num = 0;
        for(TsmLearner tsm: tsmSet){
            sb.append("The " + num + "th tsm is:" + "\n");
            if(tsmFlags.get(num)[1]){
                sb.append(tsm.catTSMtoString());
            }else {
                sb.append(tsm.toString() + "\n");
            }
            num++;
        }
        return sb.toString();
    }

}
