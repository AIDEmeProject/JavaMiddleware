package machinelearning.classifier;

/**
 * Three classes: positive, negative and unknown
 */
public enum ExtendedLabel {
    POSITIVE(1), NEGATIVE(-1), UNKNOWN(0);

    private int sign;

    /**
     * Initialize the class with integer
     * @param sign {-1,0,1}
     */
    ExtendedLabel(int sign) {
        this.sign = sign;
    }

    /**
     * @return whether the label is POSITIVE
     */
    public boolean isPositive() {
        return (sign == 1);
    }

    /**
     * @return whether the label is NEGATIVE
     */
    public boolean isNegative() {
        return (sign == -1);
    }

    /**
     * @return whether the label is UNKNOWN
     */
    public boolean isUnkown(){
        return (sign == 0);
    }

    /**
     * @return 1 if POSITIVE, -1 if NEGATIVE, 0 if UNKNOWN
     */
    public int asSign(){
        return sign;
    }

    @Override
    public String toString() {
        if(sign > 0){
            return "POSITIVE";
        }else if (sign < 0){
            return "NEGATIVE";
        }else {
            return "UNKNOWN";
        }
    }
}