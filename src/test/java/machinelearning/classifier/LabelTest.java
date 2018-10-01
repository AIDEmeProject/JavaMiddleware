package machinelearning.classifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabelTest {

    @Test
    void isPositive_PositiveAndNegativeLabels_ReturnExpectedValue() {
        assertTrue(Label.POSITIVE.isPositive());
        assertFalse(Label.NEGATIVE.isPositive());
    }

    @Test
    void isNegative_PositiveAndNegativeLabels_ReturnExpectedValue() {
        assertFalse(Label.POSITIVE.isNegative());
        assertTrue(Label.NEGATIVE.isNegative());
    }

    @Test
    void asBinary_PositiveAndNegativeLabels_ReturnExpectedValue() {
        assertEquals(1, Label.POSITIVE.asBinary());
        assertEquals(0, Label.NEGATIVE.asBinary());
    }

    @Test
    void asSign_PositiveAndNegativeLabels_ReturnExpectedValue() {
        assertEquals(1, Label.POSITIVE.asSign());
        assertEquals(-1, Label.NEGATIVE.asSign());
    }

    @Test
    void getLabelsForEachSubspace_positiveAndNegativeLabels_returnExpectedValues() {
        assertArrayEquals(new Label[] {Label.POSITIVE}, Label.POSITIVE.getLabelsForEachSubspace());
        assertArrayEquals(new Label[] {Label.NEGATIVE}, Label.NEGATIVE.getLabelsForEachSubspace());
    }

    @Test
    void toString_PositiveAndNegativeLabels_ReturnExpectedValue() {
        assertEquals("POSITIVE", Label.POSITIVE.toString());
        assertEquals("NEGATIVE", Label.NEGATIVE.toString());
    }
}