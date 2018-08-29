//package machinelearning.classifier.TSM;
//
//import data.LabeledPoint;
//import utils.AttributeValue;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashSet;
//
//
///**
// * This class deals with Three-Set partition of space spanned by one-hot encoding features that converted from categorical attributes
// *
// * @author enhui
// */
//
//public class CatTSM {
//    /**
//     * True values of categorical attribute appearing in the labeled set
//     */
//    private HashSet<Integer> truthLines;
//
//    /**
//     * False values of categorical attribute appearing in the labeled set
//     */
//    private HashSet<Integer> falseLines;
//
//
//    public CatTSM(){
//        truthLines = new HashSet<>();
//        falseLines = new HashSet<>();
//    }
//
//    public void updateCat(Collection<LabeledPoint> labeledSamples) throws IOException {
//        for(LabeledPoint t: labeledSamples) {
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
//}
