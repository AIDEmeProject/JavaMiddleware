package explore.user;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;

public class GuiUserLabel implements UserLabel {


    private boolean isPositive;

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

    GuiUserLabel(Integer label){
        boolean isPostive = (label == 1);

        this.isPositive = isPostive;
    }

}
