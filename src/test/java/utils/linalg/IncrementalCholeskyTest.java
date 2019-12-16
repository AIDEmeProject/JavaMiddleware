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

package utils.linalg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncrementalCholeskyTest {
    private IncrementalCholesky inc;

    @BeforeEach
    void setUp() {
        inc = new IncrementalCholesky();
    }

    @Test
    void getL_noIncrement_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.getL());

    }

    @Test
    void getInverse_noIncrement_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.getInverse());

    }

    @Test
    void increment_firstIncrementWithNegativeValue_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.increment(-1));
    }

    @Test
    void increment_firstIncrementWithZeroValue_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.increment(0));
    }

    @Test
    void increment_emptyFirstIncrement_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.increment());
    }

    @Test
    void increment_firstIncrementWithMoreThanOneValue_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.increment(1.0, 2.0));
    }

    @Test
    void getL_noIncrements_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.getL());
    }

    @Test
    void getL_singleIncrement_choleskyDecompositionCorrectlyComputed() {
        inc.increment(100.0);
        assertEquals(Matrix.FACTORY.make(1, 1, 10.0), inc.getL());
    }

    @Test
    void getL_threeIncrements_choleskyDecompositionCorrectlyComputed() {
        inc.increment(Vector.FACTORY.make(100));
        inc.increment(Vector.FACTORY.make(-10, 49));
        inc.increment(Vector.FACTORY.make(-3, 4, 20));

        assertEquals(Matrix.FACTORY.make(3, 3, 10.0, 0.0, 0.0, -1.0, 6.928203230275509, 0.0, -0.3, 0.5340489990004039, 4.4299877727446), inc.getL());
    }

    @Test
    void getInverse_noIncrements_throwsException() {
        assertThrows(RuntimeException.class, () -> inc.getInverse());
    }

    @Test
    void getL_singleIncrement_inverseOfCholeskyFactorCorrectlyComputed() {
        inc.increment(100.0);
        assertEquals(Matrix.FACTORY.make(1, 1, 0.1), inc.getInverse());
    }

    @Test
    void getInverse_threeIncrements_inverseOfCholeskyFactorCorrectlyComputed() {
        inc.increment(Vector.FACTORY.make(100));
        inc.increment(Vector.FACTORY.make(-10, 49));
        inc.increment(Vector.FACTORY.make(-3, 4, 20));

        assertEquals(Matrix.FACTORY.make(3, 3, 0.1, 0.0, 0.0, 0.014433756729740647, 0.14433756729740646, 0.0, 0.005031992820344931, -0.01740034900493108, 0.22573425736126798), inc.getInverse());
    }
}