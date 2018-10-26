package machinelearning.threesetmetric.TSM;

import data.DataPoint;
import data.LabeledPoint;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


/**
 * This class deals with Three-Set partition of space spanned by one-hot encoding features that converted from categorical attributes
 * Note that this class only works for one-hot encoded categorical attributes
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
     */
    public void updateCat(Collection<LabeledPoint> labeledSamples) {
        for(LabeledPoint t: labeledSamples) {
            for(int index=0;index<t.getDim();index++){
                if(t.getLabel().asSign() > 0 && t.get(index) > 0){
                    // check whether the true value is in false value list
                    if(falseLines.contains(index)){
                        throw new IllegalArgumentException("A false value: " + index + " cannot be true!");
                    }
                    truthLines.add(index);
                }else if(t.getLabel().asSign() < 0 && t.get(index) > 0){
                    // check whether the false value is in true value list
                    if(truthLines.contains(index)){
                        throw new IllegalArgumentException("A true value: " + index + " cannot be false!");
                    }
                    falseLines.add(index);
                }
            }
        }
    }


    /**
     * Check whether an attribute is positive or not
     * @param sample data point
     * @return true if the attribute belongs to positive set, false otherwise
     */
    public boolean isOnTruthLines(DataPoint sample) { return isInside(sample, truthLines); }


    /**
     * Check whether an attribute is negative or not
     * @param sample data point
     * @return true if the attribute belongs to negative set, false otherwise
     */
    public boolean isOnFalseLines(DataPoint sample) { return isInside(sample, falseLines); }


    /**
     * Check whether an attribute belongs to a set or not
     * @param sample data point
     * @return true if the attribute belongs to the specific set, false otherwise
     */
    private boolean isInside(DataPoint sample, HashSet<Integer> lines) {
        if(lines.size() == 0){
            return false;
        }

        int count = 0;
        for(int index=0; index < sample.getDim(); index++){
            if(lines.contains(index) && sample.get(index) > 0){
                count+=1;
            }
        }
        if(count > 1 ){
            throw new IllegalArgumentException(Arrays.toString(sample.getData().toArray()) + "is positive/negative to multiple features");
        }else return count == 1;
    }

    public String catTSMtoString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Truth line is:"+ truthLines.toString() + ", ");
        sb.append("False line is:"+ falseLines.toString() + "\n");
        return sb.toString();
    }


    public HashSet<Integer> getTruthLines(){return truthLines;}

    public HashSet<Integer> getFalseLines(){return falseLines;}
}
