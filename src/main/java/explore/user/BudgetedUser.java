package explore.user;

import data.DataPoint;
import machinelearning.classifier.Label;
import utils.Validator;

import java.util.Objects;

public class BudgetedUser implements User {
    private User user;
    private int budget;
    private int numberOfLabeledPoints;

    public BudgetedUser(User user, int budget) {
        Validator.assertPositive(budget);
        this.user = Objects.requireNonNull(user);
        this.budget = budget;
        this.numberOfLabeledPoints = 0;
    }

    public boolean isWilling() {
        return numberOfLabeledPoints < budget;
    }

    @Override
    public Label getLabel(DataPoint point) {
        if (!isWilling()) {
            throw new IllegalStateException("Attempting to request label after budget was reached.");
        }
        numberOfLabeledPoints++;
        return user.getLabel(point);
    }
}
