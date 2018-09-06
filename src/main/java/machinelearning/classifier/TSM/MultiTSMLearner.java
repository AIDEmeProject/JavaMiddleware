//package machinelearning.classifier.TSM;
//
//import data.DataPoint;
//import data.LabeledDataset;
//import data.LabeledPoint;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.*;
//import java.util.stream.IntStream;
//
///**
// * Given a partition of an attributes set, we create a TSM on each subspace spanned by attributes in a certain partition.
// * This class combines together all the TSMs and create an overall TSM for the original space spanned by all the attributes.
// *
// * @author enhui
// */
//
//public class MultiTSMLearner {
//    /**
//     * List of TSMs on each subspace
//     */
//    private final ArrayList<TsmLearner> tsmSet;
//
//    /**
//     * Partition of the indices of attributes
//     */
//    private final ArrayList<int[]> feaGroups;
//
//    /**
//     * The collection of points used for TSM evaluation
//     */
//    private final Collection<DataPoint> testPoints;
//
//    /**
//     * Indicator of the types of attributes and the shape of pos and neg regions
//     * first element: true --> pos in convex; false --> pos in concave
//     * second element: true --> categorical; false --> numerical
//     */
//    private final ArrayList<boolean[]> tsmFlags;
//    //final ArrayList<HashSet<Long>> truthKeySet;
//
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
//
//    /**
//     * Test the correctness of TSMs construction
//     * 0: untested; 1: all the TSMs are invalid and assigned to be null, release memory
//     */
//    private ArrayList<Integer> testStates;
//
//    /**
//     * The opposite way to create TSM -- build convex polytope for negative regions
//     */
//    private final ArrayList<TsmLearner> backupTsmSet;
//
//    /**
//     * Record the errors occurring in standard TSM
//     */
//    private final int[] errTSM;
//
//    /**
//     * Record the errors ocurring in the backup TSM
//     */
//    private final int[] errBackTSM;
//
//    /**
//     * Check whether the current way of constructing TSM has been changed
//     * 0:unchanged, 1: changed
//     */
//    private int[] isFlagChanged;
//
//    /**
//     * TSM value := |positiveSamples|/(|positiveSamples| + |uncertainSamples|)
//     */
//    double value;
//
//
//    public MultiTSMLearner(ArrayList<int[]> feaGroups, Collection<DataPoint> testPoints,  ArrayList<boolean[]> tsmFlags) throws Exception {
//        this.feaGroups = feaGroups;
//        this.testPoints = testPoints;
//        this.tsmFlags = tsmFlags;
//
//
//        tsmSet = new ArrayList<>();
//        //truthKeySet = new ArrayList<>();
//        testStates = new ArrayList<>();
//        backupTsmSet = new ArrayList<>();
//
//        errTSM = new int[feaGroups.size()];
//        errBackTSM = new int[feaGroups.size()];
//        isFlagChanged = new int[feaGroups.size()];
//        for(int i=0; i< feaGroups.size();i++){
//            errTSM[i] = 0;
//            errBackTSM[i] = 0;
//            isFlagChanged[i] = 0;
//        }
//
//        for(int[] selected_set : feaGroups){
//            TsmLearner tsm = new TsmLearner(selected_set.length, factorizeFeatures(testPoints, selected_set));
//            tsmSet.add(tsm);
//            //truthKeySet.add(getTrueKeys(selected_set));
//            //for hypothesis test
//            testStates.add(0);
//            TsmLearner backupTsm = new TsmLearner(selected_set.length, factorizeFeatures(testPoints, selected_set));
//            backupTsmSet.add(backupTsm);
//        }
//        System.out.println( "the number of tsms: " + tsmSet.size());
//
//        positiveSamples = new HashSet<>();
//        negativeSamples = new HashSet<>();
//        uncertainSamples = LabeledDataset.getIdSet(testPoints);
//    }
//
//
//
//
//    public HashSet<Long> getPositiveSamplesIds(){
//        return positiveSamples;
//    }
//
//    public HashSet<Long> getNegativeSamplesIds() {
//        return negativeSamples;
//    }
//
//    public HashSet<Long> getUncertainSamplesIds() { return uncertainSamples; }
//
//
//
//    public void updateRatio(Collection<Tuple> labeledSamples) throws Exception {
//        int threshold = Configuration.getTsmErrors();
//        for(int i=0;i < tsmSet.size();i++){
//            if(tsmFlags.get(i)[1]){
//                if(tsmSet.get(i)!=null){
//                    try{
//                        if(labeledSamples!=null){
//                            tsmSet.get(i).updateCatRatio(factorizeFeaturesUnscaled(labeledSamples, feaGroups.get(i), truthKeySet.get(i), i));
//                        }
//                    }catch (Exception e){
//                        errTSM[i]++;
//                        System.out.println("-0-");
//                        System.out.println(e.toString());
//                    }
//                    if(errTSM[i] >= threshold){
//                        tsmSet.set(i, null);
//                    }
//                }
//            } else if(testStates.get(i)==0){
//                if(tsmSet.get(i)!=null){
//                    try{
//                        if(labeledSamples!=null) {
//                            tsmSet.get(i).updateRatio(factorizeFeatures(labeledSamples, feaGroups.get(i), truthKeySet.get(i), i));
//                        }
//                        //System.out.println("partition: " + feaGroups.get(i) + " with flag: " + tsmFlags.get(i));
//                        //System.out.println(factorizeFeatures(labeledSamples, feaGroups.get(i), truthKeySet.get(i)).size());
//                    }catch (Exception e){
//                        errTSM[i]++;
////                        tsmSet.set(i, null);
//                        System.out.println("-2-");
//                        System.out.println(e.toString());
//                    }
//                }
//                if(errTSM[i] >= threshold){
//                    tsmSet.set(i, null);
//                    //System.out.println("-1-");
//                }
//
//                if(backupTsmSet.get(i)!=null){
//                    try{
//                        if(labeledSamples!=null){
//                            backupTsmSet.get(i).updateRatioNeg(factorizeFeatures(labeledSamples, feaGroups.get(i), truthKeySet.get(i), i));
//                        }
//                    }catch (Exception e){
//                        errBackTSM[i]++;
//                        System.out.println("-4-");
//                        System.out.println(e.toString());
//                    }
//                }
//                if(errBackTSM[i] >= threshold){
//                    backupTsmSet.set(i, null);
//                    //System.out.println("-3-");
//                }
//
//                boolean oldFlag = tsmFlags.get(i)[0];
//                if(tsmSet.get(i)==null && backupTsmSet.get(i) == null){
//                    testStates.set(i, 1);
//                }else if(tsmSet.get(i)==null && backupTsmSet.get(i)!=null){
//                    tsmFlags.get(i)[0]=false;
//                }else if(tsmSet.get(i)!=null && backupTsmSet.get(i)==null){
//                    tsmFlags.get(i)[0]=true;
//                }else {
//                    if(errTSM[i] <= errBackTSM[i]){
//                        tsmFlags.get(i)[0]=true;
//                    }else {
//                        tsmFlags.get(i)[0]=false;
//                    }
//                }
//                if(tsmFlags.get(i)[0]==oldFlag){
//                    isFlagChanged[i] = 0;
//                }else {
//                    isFlagChanged[i] = 1;
//                }
//
//
//                // if threshold < min(err(tsmSet[i]), backupTsmSet[i]), neither convex+, nor convex-
//                // if err(tsmSet[i]) < err(backupTsmSet[i]) and err(tsmSet[i]) < threshold, set tsmFlag to positive(convex+) and update the model
//                // if err(backupTsmSet[i]) < err(tsmSet[i]) and err(backupTsmSet[i]) < threshold, set tsmFlag to negative(convex-) and update the model
//            }
//        }
////        System.out.println("Error of convex+: " + Arrays.toString(errTSM) + "Error of convex-: " + Arrays.toString(errBackTSM));
////        System.out.println( "tsm's state of hypothesis test: " + Arrays.toString(testStates.toArray()));
////        System.out.println("Tsm flag of convex assumption is: " + Arrays.deepToString(tsmFlags.toArray()));
//    }
//
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
////                    if(!isInPositiveRegion(posSample)){
////                        posId.remove();
////                        if(isInNegativeRegion(posSample)){
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
////                    if(!isInNegativeRegion(negSample)){
////                        negId.remove();
////                        if(isInPositiveRegion(negSample)){
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
//                if (isInPositiveRegion(sample)) {
//                    i.remove();
//                    positiveSamples.add(key);
//                }else if (isInNegativeRegion(sample)){
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
//
//
//    public boolean isInPositiveRegion (Tuple sample) throws Exception {
//        ArrayList<Boolean> catTruth = new ArrayList<> ();
//        for(int i=0; i < tsmSet.size(); i++){
//            // for categorical variables, if the sample is not on the truth lines, return false
//            if(tsmFlags.get(i)[1]){
//                if(tsmSet.get(i) != null){
//                    catTruth.add(tsmSet.get(i).isOnTruthLines(factorizeCatFeatures(sample, feaGroups.get(i))));
//                } else {
//                    catTruth.add(false);
//                }
//            }else{
//                // for numerical variables
//                Tuple newSample = factorizeFeatures(sample, feaGroups.get(i));
//                boolean flag = tsmFlags.get(i)[0];
//                //System.out.println("Convex on pos: " + flag);
//                if(flag){
//                    if(tsmSet.get(i) == null || !tsmSet.get(i).isInPositiveRegion(newSample, flag)){
//                        return false;
//                    }
//                }else {
//                    if(backupTsmSet.get(i)==null || !backupTsmSet.get(i).isInPositiveRegion(newSample, flag)){
//                        return false;
//                    }
//                }
//            }
//        }
//
//        return (!catTruth.contains(false));
//    }
//
//    public boolean isInNegativeRegion (Tuple sample) throws Exception {
//        for(int i=0; i < tsmSet.size(); i++){
//            if(tsmFlags.get(i)[1]){
//                if(tsmSet.get(i) != null && tsmSet.get(i).isOnFalseLines(factorizeCatFeatures(sample, feaGroups.get(i)))){
//                    return true;
//                }
//            }else {
//                Tuple newSample = factorizeFeatures(sample, feaGroups.get(i));
//                boolean flag = tsmFlags.get(i)[0];
//                if(flag){
//                    if(tsmSet.get(i)!= null && tsmSet.get(i).isInNegativeRegion(newSample, flag)) {
//                        return true;
//                    }
//                }else {
//                    //todo: remove tsmFlags from threeset metric
//                    if(backupTsmSet.get(i)!= null && backupTsmSet.get(i).isInNegativeRegion(newSample, flag)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    public boolean isUsefulSample(Tuple sample) throws Exception {
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
//    public boolean isUncertainSample(Tuple sample) throws Exception {
//        if (isInPositiveRegion(sample)) {
//            return false;
//        }
//        if (isInNegativeRegion(sample)) {
//            return false;
//        }
//        return true;
//    }
//
//
//    public Long getRandomPositiveIdNotIn (HashSet<Long> labeledSampleIds) {
//        for (Long posSampleId: positiveSamples) {
//            if (!labeledSampleIds.contains(posSampleId)) {
//                return posSampleId;
//            }
//        }
//        return null;
//    }
//
//    public Long getRandomNegativeIdNotIn (HashSet<Long> labeledSampleIds) {
//        for (Long negSampleId: negativeSamples) {
//            if (!labeledSampleIds.contains(negSampleId)) {
//                return negSampleId;
//            }
//        }
//        return null;
//    }
//
//    public Tuple getEvaluatingTupleById (Long id) {
//        return ep.points.get(id);
//    }
//
//    public ArrayList<int[]> getErrorIndexwithFlag(){
//        ArrayList<int[]> res = new ArrayList<>();
//        for(int i = 0; i < errTSM.length; i++){
//            if(errTSM[i] >= Configuration.getTsmErrors()){
//                res.add(new int[] {i, 1});
//            }
//        }
//        for(int i = 0; i < errBackTSM.length; i++){
//            if(errBackTSM[i] >= Configuration.getTsmErrors()){
//                res.add(new int[] {i, -1});
//            }
//        }
//        if(res.size() > 0){
//            return res;
//        }else {
//            return null;
//        }
//    }
//
//    /*
//    Test whether all partitions have been null or not
//     */
//    public boolean isTSMsetNull(){
//        return (!testStates.contains(0));
//    }
//
//
//    public Collection<Tuple> factorizeFeatures(Collection<Tuple> labeledSamples, ArrayList<Integer> select_set, HashSet<Long> truthKeys, int index) throws Exception {
//        for (Tuple t : labeledSamples) {
//            // define the partial truth
//            t.setTsmLabel(truthKeys, index);
//            for (int i = 0; i < t.getScaledAV().length; i++) {
//                if (select_set.contains(i)) {
//                    t.selectAttribute(i);
//                } else {
//                    t.unselectAttribute(i);
//                }
//
//            }
//            //System.out.println("before modeling:"+ t.getSelectedScaledAV());
//        }
//        return labeledSamples;
//    }
//
//
//    /**
//     * Project data onto a subspace
//     * @param dataPoints data points to be evaluated
//     * @param select_set indices of attributes in a certain partition
//     * @return data projected onto a subspace
//     */
//    public Collection<DataPoint> factorizeFeatures(Collection<DataPoint> dataPoints, int[] select_set) {
//        Collection<DataPoint> dataPointsCopy = new ArrayList<>();
//        for(DataPoint dataPoint : dataPoints){
//            DataPoint partialPoint = new DataPoint(dataPoint.getRow(),dataPoint.getSelectedAttributes(select_set));
//            dataPointsCopy.add(partialPoint);
//        }
//        return dataPointsCopy;
//    }
//
//
//    public Tuple factorizeFeatures(Tuple labeledSample, ArrayList<Integer> select_set) throws Exception {
//        for (int i = 0; i < labeledSample.getScaledAV().length; i++) {
//            if (select_set.contains(i)) {
//                labeledSample.selectAttribute(i);
//            } else {
//                labeledSample.unselectAttribute(i);
//            }
//
//        }
//        //System.out.println("before modeling:"+ t.getSelectedScaledAV());
//        return labeledSample;
//    }
//
//
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
//
//
//    // factorization on unscaled av
//    public Collection<Tuple> factorizeFeaturesUnscaled(Collection<Tuple> labeledSamples, ArrayList<Integer> select_set, HashSet<Long> truthKeys, int index) throws Exception {
//        for (Tuple t : labeledSamples) {
//            // define the partial truth, for categorical attributes, noise labeling isn't allowed
//            t.setTsmLabel(truthKeys, index);
//            // t.setTsmCatLabel(truthKeys);
//            for (int i = 0; i < t.getUnscaledAV().length; i++) {
//                if (select_set.contains(i)) {
//                    t.selectAttribute(i);
//                } else {
//                    t.unselectAttribute(i);
//                }
//
//            }
//            //System.out.println("before modeling:"+ t.getSelectedScaledAV());
//            if(Configuration.isDebug()){
//                System.out.println("before modeling:"+ t.getSelectedUnscaledAV());
//            }
//        }
//        return labeledSamples;
//    }
//
//    public Tuple factorizeCatFeatures(Tuple labeledSample, ArrayList<Integer> select_set) throws Exception {
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
//    }
//
//}
