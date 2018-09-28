package explore.user;

import data.DataPoint;
import machinelearning.classifier.UserLabel;
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
