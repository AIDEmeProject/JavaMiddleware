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

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.ExtendedLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    private final List<TsmLearner> tsmSet;

    /**
     * Partition of the indices of attributes
     */
    private final List<int[]> feaGroups;

    /**
     * Indicator of the type of attributes and the shape of pos and neg regions
     * first element: true --> pos in convex; false --> pos in concave
     * second element: true --> categorical; false --> numerical
     */
    private final List<boolean[]> tsmFlags;

    /**
     * The opposite way to create TSM -- build convex polytope for negative regions
     */
    private final List<TsmLearner> backupTsmSet;

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
     * Test the correctness of TSMs construction. For each element:
     * 0: untested; 1: TSM is invalid and assigned to be null, release memory
     */
    private List<Integer> testStates;


    /**
     * @param feaGroups partition of attributes represented by indices
     * @param tsmFlags a list of indicators that correspond to attributes partition
     */
    public MultiTSMLearner(List<int[]> feaGroups,  List<boolean[]> tsmFlags) {
        this.feaGroups = feaGroups;
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
            TsmLearner tsm = new TsmLearner(selected_set.length);
            tsmSet.add(tsm);

            //for hypothesis test
            testStates.add(0);

            // backup tsm is constructed according to the assumption that negative region is convex
            TsmLearner backupTsm = new TsmLearner(selected_set.length);
            backupTsmSet.add(backupTsm);
        }
    }

    /**
     * Update TSM on each subspace and combine them together based on the conjunctive assumption
     * @param labeledSamples labeled examples for TSM construction
     */
    @Override
    public void update(Collection<LabeledPoint> labeledSamples) {
        for(int i=0;i < feaGroups.size();i++){
            if(tsmFlags.get(i)[1]){
                // if the second element of flags array is true, the corresponding attribute is a categorical attribute
                if(tsmSet.get(i)!=null){
                    try{
                        if(labeledSamples!=null){
                            tsmSet.get(i).updateCat(factorizeFeatures(labeledSamples, feaGroups.get(i), i));
                        }
                    } catch (IllegalArgumentException e){
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
                    } catch (IllegalArgumentException e){
                        errTSM[i]++;
                        System.out.println("-2-");
                        System.out.println(e.toString());
                    }

                    if(errTSM[i] >= threshold){
                        tsmSet.set(i, null);
                    }
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
                    if(errBackTSM[i] >= threshold){
                        backupTsmSet.set(i, null);
                    }
                }


                //todo: add automatic conflicting points check and run test experiments
                boolean oldFlag = tsmFlags.get(i)[0];
                if(tsmSet.get(i) == null && backupTsmSet.get(i) == null){
                    testStates.set(i, 1);
                }else if(tsmSet.get(i) == null && backupTsmSet.get(i) != null){
                    if(oldFlag){
                        tsmFlags.get(i)[0] = false;
                        isFlagChanged[i] = 1;
                        System.out.println("true to false");
                    }
                }else if(tsmSet.get(i) != null && backupTsmSet.get(i) == null){
                    if(!oldFlag){
                        tsmFlags.get(i)[0] = true;
                        isFlagChanged[i] = 1;
                        System.out.println("false to true");
                    }
                }else {
                    tsmFlags.get(i)[0] = errTSM[i] <= errBackTSM[i];
                }
            }
        }
    }

    /**
     * Prediction of a given point: positive, negative and unknown
     * @param point
     * @return three-class label of a given point
     */
    @Override
    public ExtendedLabel predict(DataPoint point) {


        if(isInPosRegion(point)){
            return ExtendedLabel.POSITIVE;
        }else if(isInNegRegion(point)){
            return ExtendedLabel.NEGATIVE;
        }else {
            return ExtendedLabel.UNKNOWN;
        }
    }


    /**
     * @return true if at least one partition of tsm is still running, false otherwise
     */
    public boolean isRunning(){
        return testStates.contains(0);
    }

    /**
     * @return true if any partition of tsm has failed and relabeling is required, false otherwise
     */
    public boolean triggerRelabeling(){
        return Arrays.stream(isFlagChanged).anyMatch(x -> x == 1);
    }

    /**
     * Verify if an example is positive
     * @param sample an example to be checked
     * @return true if the example is in the positive region, false otherwise
     */
    public boolean isInPosRegion (DataPoint sample) {
        ArrayList<Boolean> catTruth = new ArrayList<> ();
        for(int i=0; i < tsmSet.size(); i++){
            // for categorical variables, if the example is not on the truth lines, return false
            if(tsmFlags.get(i)[1]){
                if(tsmSet.get(i) != null){
                    catTruth.add(tsmSet.get(i).isOnTruthLines(factorizeFeatures(sample, feaGroups.get(i))));
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
    public boolean isInNegRegion (DataPoint sample) {
        for(int i=0; i < tsmSet.size(); i++){
            if(tsmFlags.get(i)[1]){
                if(tsmSet.get(i) != null && tsmSet.get(i).isOnFalseLines(factorizeFeatures(sample, feaGroups.get(i)))){
                    return true;
                }
            }else {
                DataPoint newSample = factorizeFeatures(sample, feaGroups.get(i));
                boolean flag = tsmFlags.get(i)[0];
                if(flag){
                    if(tsmSet.get(i)!= null && tsmSet.get(i).isInConcaveRegion(newSample, true)) {
                        return true;
                    }
                } else {
                    //todo: remove tsmFlags from threeset metric
                    if(backupTsmSet.get(i)!= null && backupTsmSet.get(i).isInConcaveRegion(newSample, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Collection<LabeledPoint> factorizeFeatures(Collection<LabeledPoint> labeledSamples, int[] select_set, int index) {
        Collection<LabeledPoint> dataPointsCopy = new ArrayList<>();
        for(LabeledPoint dataPoint : labeledSamples){
//            System.out.println("the size of label list: " + dataPoint.getLabel().getLabelsForEachSubspace().length);
//            System.out.println("the type of labels: " + dataPoint.getLabel().getClass());
            Label label = dataPoint.getLabel().getLabelsForEachSubspace()[index];
            LabeledPoint partialPoint = dataPoint.getSelectedAttributes(select_set, label);
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
    private static DataPoint factorizeFeatures(DataPoint testSample, int[] select_set) {
        return testSample.getSelectedAttributes(select_set);
    }

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
