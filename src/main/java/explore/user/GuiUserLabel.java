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
