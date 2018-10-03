package machinelearning.classifier;

import explore.user.UserLabel;
import machinelearning.threesetmetric.LabelGroup;

/**
 * Binary labels, used for classification. There are only two instances of this class: POSITIVE and NEGATIVE.
 */
public class Label implements UserLabel {
    /**
     * Whether this label is the POSITIVE or the NEGATIVE one
     */
    private boolean isPositive;

    private LabelGroup labelGroup = null;


    public Label(boolean isPositive) {
        this.isPositive = isPositive;
    }

    /**
     * POSITIVE label
     */
    public final static Label POSITIVE = new Label(true);

    /**
     * NEGATIVE label
     */
    public final static Label NEGATIVE = new Label(false);

    /**
     * @return whether the label is POSITIVE
     */
    public boolean isPositive() {
        return isPositive;
    }

    /**
     * @return whether the label is NEGATIVE
     */
    public boolean isNegative() {
        return !isPositive;
    }

    // todo: this function is problematic
//    @Override
//    public Label[] getLabelsForEachSubspace() {
//        return new Label[] {isPositive ? POSITIVE : NEGATIVE};
//    }

    public void setLabelGroup(Label[] partialLabels){
        labelGroup = new LabelGroup(partialLabels);
    }

    @Override
    public Label[] getLabelsForEachSubspace() { return labelGroup.getLabelsForEachSubspace();}

    @Override
    public String toString() {
        return isPositive() ? "POSITIVE" : "NEGATIVE";
    }
}
