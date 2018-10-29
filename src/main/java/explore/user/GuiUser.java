package explore.user;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;

public class GuiUser implements User {

    private LabeledPoint labeledPoint;

    @Override
    public Label getLabel(DataPoint point) {

        return null;
    }


    public void setLabel(LabeledPoint point){

        this.labeledPoint = point;
    }

}
