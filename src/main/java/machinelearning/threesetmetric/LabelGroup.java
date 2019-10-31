package machinelearning.threesetmetric;

import explore.user.UserLabel;
import machinelearning.classifier.Label;
import utils.Validator;

import java.util.Arrays;

/**
 * A LabelGroup will store a collection of partial labels given by the user. The final label is considered to be the
 * CONJUNCTION of all partial labels (i.e. POSITIVE if, and only if, all partial labels are POSITIVE).
 */
public class LabelGroup implements UserLabel {
    /**
     * Array of partial labels
     */
    private final Label[] partialLabels;

    /**
     * Whether the conjunction of partial labels is positive or not
     */
    private final boolean isPositive;

    /**
     * @param partialLabels: array of partial labels
     * @throws IllegalArgumentException if input is empty
     */
    public LabelGroup(Label... partialLabels) {
        Validator.assertNotEmpty(partialLabels);

        this.partialLabels = partialLabels;
        this.isPositive = computeLabelsConjunction();
    }

    private boolean computeLabelsConjunction() {
        for (Label label: partialLabels) {
            if (label.isNegative()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public boolean isNegative() {
        return !isPositive;
    }

    @Override
    public Label[] getLabelsForEachSubspace() {
        return partialLabels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelGroup that = (LabelGroup) o;
        return Arrays.equals(partialLabels, that.partialLabels);
    }

    @Override
    public String toString() {
        return Arrays.toString(partialLabels);
    }
}
