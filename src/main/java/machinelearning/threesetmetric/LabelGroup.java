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

package machinelearning.threesetmetric;

import explore.user.UserLabel;
import machinelearning.classifier.Label;
import utils.Validator;

import java.util.Arrays;

/**
 * A LabelGroup will store a collection of partial labels given by the user. The final label is considered to be the
 * CONJUNCTION of all partial labels (i.e. POSITIVE if, and only if, all partial labels are POSITIVE).
 */
public class LabelGroup implements UserLabel {
    /**
     * Array of partial labels
     */
    private final Label[] partialLabels;

    /**
     * Whether the conjunction of partial labels is positive or not
     */
    private final boolean isPositive;

    /**
     * @param partialLabels: array of partial labels
     * @throws IllegalArgumentException if input is empty
     */
    public LabelGroup(Label... partialLabels) {
        Validator.assertNotEmpty(partialLabels);

        this.partialLabels = partialLabels;
        this.isPositive = computeLabelsConjunction();
    }

    private boolean computeLabelsConjunction() {
        for (Label label: partialLabels) {
            if (label.isNegative()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public boolean isNegative() {
        return !isPositive;
    }

    @Override
    public Label[] getLabelsForEachSubspace() {
        return partialLabels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelGroup that = (LabelGroup) o;
        return Arrays.equals(partialLabels, that.partialLabels);
    }

    @Override
    public String toString() {
        return Arrays.toString(partialLabels);
    }
}
