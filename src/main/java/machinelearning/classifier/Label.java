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

    @Override
    public Label[] getLabelsForEachSubspace() {
        return new Label[] {isPositive ? POSITIVE : NEGATIVE};
    }

    @Override
    public String toString() {
        return isPositive() ? "POSITIVE" : "NEGATIVE";
    }
}
