package data;

import machinelearning.classifier.Label;

public enum ExtendedLabel {
    POSITIVE, NEGATIVE, UNKNOWN;

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

    public static ExtendedLabel fromLabel(Label label) {
        return label == Label.POSITIVE ? POSITIVE : NEGATIVE;
    }
}
