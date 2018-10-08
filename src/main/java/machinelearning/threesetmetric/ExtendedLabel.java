package machinelearning.threesetmetric;

import explore.user.UserLabel;
import machinelearning.classifier.Label;

/**
 * This enum is basically an extension of {@link machinelearning.classifier.Label} for {@link ExtendedClassifier}.
 * Give a data point, a ExtendedClassifier can predict it to belong into three classes:
 *
 *  - POSITIVE: interesting to the user
 *  - NEGATIVE: uninteresting to the user
 *  - UNKNOWN: unsure about the true label
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
        return sign == 1;
    }

    /**
     * @return whether the label is NEGATIVE
     */
    public boolean isNegative() {
        return sign == -1;
    }

    /**
     * @return whether the label is UNKNOWN
     */
    public boolean isUnknown(){
        return sign == 0;
    }

    /**
     * Attempts to convert this ExtendedLabel into the corresponding {@link Label} object.
     * @return a Label object of same name (i.e. POSITIVE/NEGATIVE is converted into POSITIVE/NEGATIVE)
     * @throws IllegalArgumentException if {@code this} is UNKNOWN
     */
    public Label toLabel() {
        switch (this) {
            case POSITIVE:
                return Label.POSITIVE;
            case NEGATIVE:
                return Label.NEGATIVE;
            default:
                throw new IllegalArgumentException("Cannot convert UNKNOWN label.");
        }
    }

    /**
     * @param label a {@link Label} used in Machine Learning classifiers
     * @return an ExtendedLabel of same name (i.e. POSITIVE/NEGATIVE is converted into POSITIVE/NEGATIVE)
     */
    public static ExtendedLabel fromLabel(UserLabel label) {
        return label.isPositive() ? POSITIVE : NEGATIVE;
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