package machinelearning.classifier;

/**
 * Binary labels enum. There are only two possibilities: POSITIVE and NEGATIVE labels.
 */
public enum Label {
    POSITIVE(true), NEGATIVE(false);

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
     * @return 1 if POSITIVE, 0 if NEGATIVE
     */
    public int asBinary(){
        return isPositive ? 1 : 0;
    }

    /**
     * @return 1 if POSITIVE, -1 if NEGATIVE
     */
    public int asSign(){
        return isPositive ? 1 : -1;
    }
}
