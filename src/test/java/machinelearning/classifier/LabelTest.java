/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
    void fromSign_negativeAndPositiveValues_returnsNegativeAndPositiveLabelsRespectively() {
        assertEquals(Label.NEGATIVE, Label.fromSign(-1));
        assertEquals(Label.POSITIVE, Label.fromSign(1));
    }

    @Test
    void fromSign_zeroValue_returnsNegative() {
        assertEquals(Label.NEGATIVE, Label.fromSign(0));
    }

    @Test
    void toString_PositiveAndNegativeLabels_ReturnExpectedValue() {
        assertEquals("POSITIVE", Label.POSITIVE.toString());
        assertEquals("NEGATIVE", Label.NEGATIVE.toString());
    }
}