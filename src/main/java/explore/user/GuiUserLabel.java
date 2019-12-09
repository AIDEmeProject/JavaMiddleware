package explore.user;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedLabel;

public class GuiUserLabel implements UserLabel {


    private boolean isPositive;

    private int label;

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public boolean isNegative() {
        return false;
    }

    @Override
    public Label[] getLabelsForEachSubspace() {
        return new Label[0];
    }


    public static GuiUserLabel fromExtendedLabel(ExtendedLabel label){
        return new GuiUserLabel(label.asSign());
    }
    public GuiUserLabel(Integer label){
        boolean isPostive = (label == 1);

        this.isPositive = isPostive;
        this.label = label;
    }

    public GuiUserLabel(int label){
        boolean isPostive = (label == 1);
        this.label = label;
        this.isPositive = isPostive;
    }

    public int asSign(){
        return this.label;
    }
    public int getLabel(){ return this.label; }
}
