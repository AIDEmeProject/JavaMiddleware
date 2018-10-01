package machinelearning.classifier;

import explore.user.UserLabel;

/**
 * Binary labels, used for classification. There are only two instances of this class: POSITIVE and NEGATIVE.
 */
public class Label implements UserLabel {
    /**
     * Whether this label is the POSITIVE or the NEGATIVE one
     */
    private boolean isPositive;

    private Label(boolean isPositive) {
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

    @Override
    public Label[] getLabelsForEachSubspace() {
        return new Label[] {isPositive ? POSITIVE : NEGATIVE};
    }

    @Override
    public String toString() {
        return isPositive() ? "POSITIVE" : "NEGATIVE";
    }
}
