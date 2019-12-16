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

package machinelearning.classifier;

import explore.user.UserLabel;

/**
 * Binary labels, used for classification. There are only two instances of this class: POSITIVE and NEGATIVE.
 */
public enum Label implements UserLabel {
    POSITIVE(true), NEGATIVE(false);

    /**
     * Whether this label is the POSITIVE or the NEGATIVE one
     */
    private boolean isPositive;

    Label(boolean isPositive) {
        this.isPositive = isPositive;
    }

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

    /**
     * @param value a real number
     * @return POSITIVE if {@code value} is positive, NEGATIVE otherwise
     */
    public static Label fromSign(double value) {
        return value > 0 ? POSITIVE : NEGATIVE;
    }

    @Override
    public Label[] getLabelsForEachSubspace() {
        return new Label[] {isPositive ? POSITIVE : NEGATIVE};
    }

    @Override
    public String toString() {
        return isPositive() ? "POSITIVE" : "NEGATIVE";
    }
}
