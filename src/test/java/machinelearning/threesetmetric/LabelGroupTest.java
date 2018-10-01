package machinelearning.threesetmetric;

import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabelGroupTest {
    private Label[] labels;
    private LabelGroup labelGroup;

    @BeforeEach
    void setUp() {
        labels = new Label[]{Label.POSITIVE, Label.NEGATIVE};
        labelGroup = new LabelGroup(labels);
    }

    @Test
    void constructor_emptyArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabelGroup(new Label[0]));
    }

    @Test
    void isPositive_allPositiveLabels_returnsTrue() {
        labels = new Label[]{Label.POSITIVE};
        labelGroup = new LabelGroup(labels);
        assertTrue(labelGroup.isPositive());
    }

    @Test
    void isPositive_oneNegativeLabel_returnsFalse() {
        labels = new Label[]{Label.POSITIVE, Label.NEGATIVE};
        labelGroup = new LabelGroup(labels);
        assertFalse(labelGroup.isPositive());
    }

    @Test
    void isNegative_allPositiveLabels_returnsTrue() {
        labels = new Label[]{Label.POSITIVE};
        labelGroup = new LabelGroup(labels);
        assertFalse(labelGroup.isNegative());
    }

    @Test
    void isNegative_oneNegativeLabel_returnsFalse() {
        labels = new Label[]{Label.POSITIVE, Label.NEGATIVE};
        labelGroup = new LabelGroup(labels);
        assertTrue(labelGroup.isNegative());
    }

    @Test
    void getLabelsForEachSubspace_anyLabelGroup_returnsLabelsFromConstructor() {
        assertArrayEquals(labels, labelGroup.getLabelsForEachSubspace());
    }
}