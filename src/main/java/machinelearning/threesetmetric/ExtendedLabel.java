package machinelearning.threesetmetric;

import explore.user.UserLabel;
import machinelearning.classifier.Label;

/**
 * This enum is basically an extension of {@link Label} for {@link machinelearning.threesetmetric.ExtendedClassifier}.
 * Give a data point, a ExtendedClassifier can predict it to belong into three classes:
 *
 *  - POSITIVE: interesting to the user
 *  - NEGATIVE: uninteresting to the user
 *  - UNKNOWN: unsure about the true label
 */
public enum ExtendedLabel {
    POSITIVE, NEGATIVE, UNKNOWN;

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
}
