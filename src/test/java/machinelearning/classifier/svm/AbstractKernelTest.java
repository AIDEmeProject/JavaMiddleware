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

import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractKernelTest {
    protected Kernel kernel;

    @Test
    void compute_vectorsOfDifferentLengths_throwsException() {
        assertThrows(RuntimeException.class, () -> kernel.compute(Vector.FACTORY.zeros(1), Vector.FACTORY.zeros(2)));
    }

    void assertKernelFunctionIsCorrect(double expected, double[] arr1, double[] arr2) {
        Vector x, y;
        x = Vector.FACTORY.make(arr1);
        y = Vector.FACTORY.make(arr2);
        assertEquals(expected, kernel.compute(x, y), 1e-10);
    }

    void assertKernelVectorIsCorrect(double[] expected, double[][] arr1, double[] arr2) {
        Matrix x = Matrix.FACTORY.make(arr1);
        Vector y = Vector.FACTORY.make(arr2);
        assertEquals(Vector.FACTORY.make(expected), kernel.compute(x, y));
    }

    void assertKernelMatrixIsCorrectlyComputed(double[][] expected, double[][] toCompute) {
        assertEquals(Matrix.FACTORY.make(expected), kernel.compute(Matrix.FACTORY.make(toCompute)));
    }
}