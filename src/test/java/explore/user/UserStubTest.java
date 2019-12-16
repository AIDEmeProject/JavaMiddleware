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

package explore.user;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserStubTest {
    private IndexedDataset points;
    private UserStub user;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        for (int i = 0; i < 4; i++) {
            builder.add(i, new double[]{i+1});
        }
        points = builder.build();

        Set<Long> positiveKeys = new HashSet<>(Arrays.asList(1L, 2L));
        user = new UserStub(positiveKeys);
    }

    @Test
    void constructor_emptyKeySet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new UserStub(new HashSet<>()));
    }

    @Test
    void getLabel_testAllIndexes_returnsCorrectLabels() {
        Label[] labels = new Label[] {Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE};
        int i = 0;
        for (DataPoint point : points) {
            assertEquals(labels[i++], user.getLabel(point));
        }
    }

    @Test
    void getAllLabels_callGetAllLabels_returnsLabelsArray() {
        assertArrayEquals(new Label[] {Label.NEGATIVE,Label.POSITIVE,Label.POSITIVE,Label.NEGATIVE}, user.getLabel(points));
    }

}