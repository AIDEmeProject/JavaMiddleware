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

package data.preprocessing;

import data.IndexedDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardScalerTest {
    private Matrix points;
    private IndexedDataset dataset;
    private StandardScaler scaler;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(10, new double[] {-1, -2});
        builder.add(20, new double[] { 0, 1});
        builder.add(30, new double[] {1, 2});
        dataset = builder.build();
        points = dataset.getData();
        scaler = StandardScaler.fit(points);
    }

    @Test
    void fit_columnOfZeroStandardDeviation_throwsException() {
        points = Matrix.FACTORY.make(3, 1, 1, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> StandardScaler.fit(points));
    }

    @Test
    void transform_validInput_inputCorrectlyNormalized() {
        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(scaler.transform(points), 1e-8));
    }

    @Test
    void transform_indexedDataset_correctOutput() {
        IndexedDataset transformed = scaler.transform(dataset);
        assertEquals(transformed.getIndexes(), dataset.getIndexes());

        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(transformed.getData(), 1e-8));
    }

    @Test
    void fitAndTransform_indexedDataset_outputHasSameIndex() {
        IndexedDataset transformed = StandardScaler.fitAndTransform(dataset);
        assertEquals(transformed.getIndexes(), dataset.getIndexes());

        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(transformed.getData(), 1e-8));
    }
}