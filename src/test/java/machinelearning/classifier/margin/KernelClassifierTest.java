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

package machinelearning.classifier.margin;

import machinelearning.classifier.svm.Kernel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KernelClassifierTest {
    private Matrix support;
    private Kernel kernel;
    private KernelClassifier classifier;

    @BeforeEach
    void setUp() {
        kernel = spy(Kernel.class);
        when(kernel.compute((Vector) any(), any())).thenReturn(1.);
        support = Matrix.FACTORY.make(3, 2, 1, 1, 2, 3, 4, 5);
        classifier = new KernelClassifier(1, Vector.FACTORY.make(-2,3,0), support, kernel);
    }

    @Test
    void hyperPlaneConstructor_NullLinearClassifier_throwsException() {
        assertThrows(NullPointerException.class, () -> new KernelClassifier(null, support, kernel));
    }

    @Test
    void hyperPlaneConstructor_nullKernel_throwsException() {
        assertThrows(NullPointerException.class,
                () -> new KernelClassifier(mock(HyperPlane.class), support, null));
    }

    @Test
    void hyperPlaneConstructor_differentLinearClassifierDimensionAndSupportVector_throwsException() {
        HyperPlane hyperPlane = mock(HyperPlane.class);
        when(hyperPlane.dim()).thenReturn(2);

        assertThrows(IllegalArgumentException.class,
                () -> new KernelClassifier(hyperPlane, mock(Matrix.class), kernel));
    }

    @Test
    void margin_alwaysOneKernel_returnsCorrectMargin() {
        assertEquals(2, classifier.margin(Vector.FACTORY.make(1, 2)));
    }
}