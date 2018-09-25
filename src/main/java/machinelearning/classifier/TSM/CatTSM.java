package machinelearning.classifier.TSM;

import data.DataPoint;
import data.LabeledPoint;

import java.util.*;


/**
 * This class deals with Three-Set partition of space spanned by one-hot encoding features that converted from categorical attributes
 *
 * @author enhui
 */

public class CatTSM {
    /**
     * True values of categorical attribute appearing in the labeled set
     */
    private HashSet<Integer> truthLines;

    /**
     * False values of categorical attribute appearing in the labeled set
     */
    private HashSet<Integer> falseLines;


    /**
     * Store the information of categorical attributes
     */
    public CatTSM( ){
        truthLines = new HashSet<>();
        falseLines = new HashSet<>();
    }

    /**
     * Update the recorded information about categorical attributes
     * @param labeledSamples
     * @param indices indices of categorical attributes
     */
    public void updateCat(Collection<LabeledPoint> labeledSamples, int[] indices) {
        for(LabeledPoint t: labeledSamples) {
            HashMap<Integer, Double> selectedAttributesMap = t.getSelectedAttributesMap(indices);
            for(Integer index : selectedAttributesMap.keySet()){
                if(t.getLabel().asSign() > 0 && selectedAttributesMap.get(index) > 0){
                    // check whether the true value is in false value list
                    if(falseLines.contains(index)){
                        throw new IllegalArgumentException("A false value: " + index + " cannot be true!");
                    }
                    truthLines.add(index);
                }else if(t.getLabel().asSign() < 0 && selectedAttributesMap.get(index) > 0){
                    // check whether the false value is in true value list
                    if(truthLines.contains(index)){
                        throw new IllegalArgumentException("A true value: " + index + " cannot be false!");
                    }
                    falseLines.add(index);
                }
            }
        }
//        System.out.println("truthLines contains: " + Arrays.toString(truthLines.toArray()));
//        System.out.println("falseLines contains: " + Arrays.toString(falseLines.toArray()));
    }


    /**
     * Check whether an attribute is positive or not
     * @param sample data point
     * @param indices indices of categorical attributes
     * @return true if the attribute belongs to positive set, false otherwise
     */
    public boolean isOnTruthLines(DataPoint sample, int[] indices) { return isInside(sample, truthLines, indices); }


    /**
     * Check whether an attribute is negative or not
     * @param sample data point
     * @param indices indices of categorical attributes
     * @return true if the attribute belongs to negative set, false otherwise
     */
    public boolean isOnFalseLines(DataPoint sample, int[] indices) {
        return isInside(sample, falseLines, indices);
    }


    /**
     * Check whether an attribute belongs to a set or not
     * @param sample data point
     * @param indices indices of categorical attributes
     * @return true if the attribute belongs to the specific set, false otherwise
     */
    private boolean isInside(DataPoint sample, HashSet<Integer> lines, int[] indices) {
        if(lines.size() == 0){
            return false;
        }

        int count = 0;
        HashMap<Integer, Double> selectedAttributesMap = sample.getSelectedAttributesMap(indices);
        for(Integer index: selectedAttributesMap.keySet()){
            if(lines.contains(index) && selectedAttributesMap.get(index) > 0){
                count+=1;
            }
        }
        if(count > 1 ){
            throw new IllegalArgumentException(Arrays.toString(sample.getSelectedAttributes(indices)) + "is positive/negative to multiple features");
        }else return count == 1;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Truth line is:"+ truthLines.toString() + "\n");
        sb.append("False line is:"+ falseLines.toString() + "\n");
        return sb.toString();
    }
}
