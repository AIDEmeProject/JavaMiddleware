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
import utils.Validator;

import java.util.Objects;

/**
 * A BudgetedUser represents a user who can only label a limited number of data points. It is meant to be used for testing
 * new algorithms, and not during a real scenario.
 */
public final class BudgetedUser implements User {
    /**
     * A {@link User} who performs the labeling
     */
    private final User user;

    /**
     * Upper bound on the number of points to label
     */
    private final int budget;


    /**
     * Number of labeled points so far
     */
    private int numberOfLabeledPoints;

    /**
     * @throws IllegalArgumentException if budget is not positive
     * @throws NullPointerException if user is null
     */
    public BudgetedUser(User user, int budget) {
        Validator.assertPositive(budget);
        this.user = Objects.requireNonNull(user);
        this.budget = budget;
        this.numberOfLabeledPoints = 0;
    }

    /**
     * @return number of labeled points so far
     */
    public int getNumberOfLabeledPoints() {
        return numberOfLabeledPoints;
    }

    /**
     * @return true if the user is still willing to continue labeling, i.e. less the "budget" data points have been labeled so far.
     */
    public boolean isWilling() {
        return numberOfLabeledPoints < budget;
    }

    /**
     * @param point: point to label
     * @return the label of the requested data point
     * @throws IllegalStateException if attempting to label a data point when the budget has already been met.
     */
    @Override
    public UserLabel getLabel(DataPoint point) {
        if (!isWilling()) {
            throw new IllegalStateException("Attempting to request label after budget was reached.");
        }

        numberOfLabeledPoints++;

        return user.getLabel(point);
    }
}
