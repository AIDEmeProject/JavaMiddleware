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
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactoredUserTest {
    private List<Set<Long>> positiveKeysPerSubspace;
    private FactoredUser factoredUser;

    @Test
    void constructor_emptyListOfPositiveKeys_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new FactoredUser(new ArrayList<>()));
    }

    @Test
    void constructor_listContainsEmptyPositiveKeysSet_throwsException() {
        positiveKeysPerSubspace = new ArrayList<>(new HashSet<>());
        assertThrows(IllegalArgumentException.class, () -> new FactoredUser(positiveKeysPerSubspace));
    }

    @Test
    void getLabel_dataPointBelongsToNoIdSet_returnsExpectedLabelGroup() {
        Label[] expected = new Label[]{Label.NEGATIVE, Label.NEGATIVE};
        assertLabelingIsCorrect(0, expected, new Long[]{2L}, new Long[]{1L});
    }

    @Test
    void getLabel_dataPointBelongsToSingleIdSet_returnsExpectedLabelGroup() {
        Label[] expected = new Label[]{Label.POSITIVE, Label.NEGATIVE};
        assertLabelingIsCorrect(0, expected, new Long[]{0L}, new Long[]{1L});
    }

    @Test
    void getLabel_dataPointBelongsToBothIdSet_returnsExpectedLabelGroup() {
        Label[] expected = new Label[]{Label.POSITIVE, Label.POSITIVE};
        assertLabelingIsCorrect(0, expected, new Long[]{0L}, new Long[]{0L, 1L});
    }

    private void assertLabelingIsCorrect(long id, Label[] expected, Long[]... lists) {
        DataPoint dataPoint = new DataPoint(id, Vector.FACTORY.zeros(1));
        setFactoredUserFromArrays(lists);
        assertEquals(new LabelGroup(expected), factoredUser.getLabel(dataPoint));
    }

    private void setFactoredUserFromArrays(Long[]... lists) {
        positiveKeysPerSubspace = new ArrayList<>();
        for (Long[] ids : lists) {
            positiveKeysPerSubspace.add(new HashSet<>(Arrays.asList(ids)));
        }
        factoredUser = new FactoredUser(positiveKeysPerSubspace);

    }

}