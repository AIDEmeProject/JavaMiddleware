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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CholeskyDecompositionTest {
    @Test
    void constructor_nonSquareMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 1, 10.0, 20.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void constructor_NonSymmetricMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 10.0, 20.0, 30.0, 40.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void constructor_SymmetricButNotPositiveDefiniteMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 1.0, 0, 0, -1.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void constructor_PositiveSemiDefiniteMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 1.0, 0, 0, 0.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void getL_PositiveDefiniteDiagonalMatrix_returnsSquareRoot() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 4.0, 0, 0, 9.0);
        assertEquals(Matrix.FACTORY.make(2, 2, 2.0, 0, 0, 3.0), new CholeskyDecomposition(matrix).getL());
    }

    @Test
    void getL_PositiveDefiniteNonDiagonalMatrix_returnsCorrectCholeskyFactorization() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 4.0, -2.0, -2.0, 10.0);
        assertEquals(Matrix.FACTORY.make(2, 2, 2.0, 0.0, -1.0, 3.0), new CholeskyDecomposition(matrix).getL());
    }
}