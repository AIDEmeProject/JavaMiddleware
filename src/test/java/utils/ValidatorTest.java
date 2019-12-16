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

package utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {
    @Test
    void assertIsNotEmpty_emptyCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNotEmpty(new ArrayList<>()));
    }

    @Test
    void assertIsNotEmpty_nonEmptyCollection_doNotThrowException() {
        Validator.assertNotEmpty(Collections.singletonList(1));
    }

    @Test
    void assertIsNotEmpty_emptyIntegerArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNotEmpty(new int[] {}));
    }

    @Test
    void assertIsNotEmpty_nonEmptyIntegerArray_doNotThrowException() {
        Validator.assertNotEmpty(new int[] {1});
    }

    @Test
    void assertIsNotEmpty_emptyDoubleArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNotEmpty(new double[] {}));
    }

    @Test
    void assertIsNotEmpty_nonEmptyDoubleArray_doNotThrowException() {
        Validator.assertNotEmpty(new double[] {1.});
    }

    @Test
    void assertEquals_equalValues_doNotThrowException() {
        Validator.assertEquals(0,0);
    }

    @Test
    void assertEquals_distinctValues_doNotThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertEquals(0,1));
    }

    @Test
    void assertEqualLengths_differentSizeIntegerArrays_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertEqualLengths(new int[] {}, new int[] {1}));
    }

    @Test
    void assertEqualLengths_equalSizeIntegerArrays_doNotThrowException() {
        Validator.assertEqualLengths(new int[] {2}, new int[] {1});
    }

    @Test
    void assertEqualLengths_differentSizeDoubleArrays_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertEqualLengths(new double[] {}, new double[] {1}));
    }

    @Test
    void assertEqualLengths_equalSizeDoubleArrays_doNotThrowException() {
        Validator.assertEqualLengths(new double[] {1.}, new double[] {2.});
    }

    @Test
    void assertPositive_positiveInteger_doNotThrowException() {
        Validator.assertPositive(1);
    }

    @Test
    void assertPositive_negativeInteger_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertPositive(-1));
    }

    @Test
    void assertPositive_positiveDouble_doNotThrowException() {
        Validator.assertPositive(1.);
    }

    @Test
    void assertPositive_negativeDouble_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertPositive(-1.));
    }

    @Test
    void assertPositive_zero_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertPositive(0));
    }

    @Test
    void assertNonNegative_positiveInteger_doNotThrowException() {
        Validator.assertNonNegative(1);
    }

    @Test
    void assertNonNegative_negativeInteger_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNonNegative(-1));
    }

    @Test
    void assertNonNegative_positiveDouble_doNotThrowException() {
        Validator.assertNonNegative(1.);
    }

    @Test
    void assertNonNegative_negativeDouble_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNonNegative(-1.));
    }

    @Test
    void assertNonNegative_zero_doNotThrowException() {
        Validator.assertNonNegative(0);
    }


}