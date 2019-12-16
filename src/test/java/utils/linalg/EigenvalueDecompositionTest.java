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

class EigenvalueDecompositionTest {
    private Matrix matrix;
    private EigenvalueDecomposition decomposition;

    @BeforeEach
    void setUp() {
        matrix = Matrix.FACTORY.make(2, 2, 1, 0, 0, -1);
        decomposition = new EigenvalueDecomposition(matrix);
    }

    @Test
    void constructor_nonSquareMatrix_throwsException() {
        matrix = Matrix.FACTORY.make(2, 1, 10.0, 20.0);
        assertThrows(RuntimeException.class, () -> new EigenvalueDecomposition(matrix));
    }

    @Test
    void constructor_matrixWithoutRealDecomposition_throwsException() {
        matrix = Matrix.FACTORY.make(2, 2, 0, -1, 1, 0);
        assertThrows(RuntimeException.class, () -> new EigenvalueDecomposition(matrix));
    }

    @Test
    void getEigenvalue_negativeIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvalue(-1));
    }

    @Test
    void getEigenvalue_indexEqualToMatrixDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvalue(matrix.rows()));
    }

    @Test
    void getEigenvector_negativeIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvector(-1));
    }

    @Test
    void getEigenvector_indexEqualToMatrixDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvector(matrix.rows()));
    }

    @Test
    void getEigenvalue_diagonalMatrix_returnsEachElementOfDiagonalInOrder() {
        assertEquals(1, decomposition.getEigenvalue(0));
        assertEquals(-1, decomposition.getEigenvalue(1));
    }

    @Test
    void getEigenvector_diagonalMatrix_returnsCanonicalVectors() {
        assertEquals(Vector.FACTORY.make(1, 0), decomposition.getEigenvector(0));
        assertEquals(Vector.FACTORY.make(0, 1), decomposition.getEigenvector(1));
    }

    @Test
    void getEigenvector_nonDiagonalMatrix_characteristicEquationsAreSatisfied() {
        matrix = Matrix.FACTORY.make(2, 2, 0, 1, 1, 0);
        decomposition = new EigenvalueDecomposition(matrix);
        assertEigenvectorSatisfyCharacteristicEquation();
    }

    @Test
    void getEigenvector_eigenvectorMatrixDifferentFromIdentity_characteristicEquationsAreSatisfied() {
        matrix = Matrix.FACTORY.make(3, 3, 1, -0.5, -0.5, -0.5, 1, -0.1, -0.5, -0.1, 1);
        decomposition = new EigenvalueDecomposition(matrix);
        assertEigenvectorSatisfyCharacteristicEquation();
    }

    private void assertEigenvectorSatisfyCharacteristicEquation() {
        for (int i = 0; i < matrix.rows(); i++) {
            Vector leftHandSide = matrix.multiply(decomposition.getEigenvector(i));
            Vector rightHandSide = decomposition.getEigenvector(i).scalarMultiply(decomposition.getEigenvalue(i));
            assertTrue(leftHandSide.equals(rightHandSide, 1e-10));
        }
    }
}