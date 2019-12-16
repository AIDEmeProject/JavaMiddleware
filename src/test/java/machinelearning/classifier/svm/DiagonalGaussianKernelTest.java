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

package machinelearning.classifier.svm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiagonalGaussianKernelTest extends AbstractKernelTest {
    private Vector diagonal;

    @BeforeEach
    void setUp() {
        diagonal = Vector.FACTORY.make(2.0, 3.0);
        kernel = new DiagonalGaussianKernel(diagonal);
    }

    @Test
    void constructor_negativeValueInDiagonal_throwsException() {
        diagonal = Vector.FACTORY.make(-1);
        assertThrows(IllegalArgumentException.class, () -> new DiagonalGaussianKernel(diagonal));
    }


    @Test
    void constructor_zeroValueInDiagonal_throwsException() {
        diagonal = Vector.FACTORY.make(0);
        assertThrows(IllegalArgumentException.class, () -> new DiagonalGaussianKernel(diagonal));
    }

    @Test
    void compute_gammaEqualsToTwo_returnsExpectedValue() {
        assertKernelFunctionIsCorrect(Math.exp(-21), new double[]{1, 2}, new double[]{-2, 3});
    }

    @Test
    void computeKernelMatrix_gammaEqualsToTwo_returnsExpectedValue() {
        double[][] toCompute = new double[][] {{1, 2}, {-2, 3}};
        double[][] expected = new double[][] {{1, Math.exp(-21)}, {Math.exp(-21), 1}};
        assertKernelMatrixIsCorrectlyComputed(expected, toCompute);
    }
}
