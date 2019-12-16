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

package machinelearning.classifier;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.VersionSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class MajorityVoteLearnerTest {
    private MajorityVoteLearner learner;

    @BeforeEach
    void setUp() {
        learner = new MajorityVoteLearner(mock(VersionSpace.class), 1);
    }

    @Test
    void constructor_NullInputVersionSpace_throwsException() {
        assertThrows(NullPointerException.class, () -> new MajorityVoteLearner(null, 1));
    }

    @Test
    void constructor_NegativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteLearner(mock(VersionSpace.class), -1));
    }

    @Test
    void constructor_ZeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MajorityVoteLearner(mock(VersionSpace.class), 0));
    }

    @Test
    void fit_mockedVersionSpace_versionSpaceIsSampledOnceWithCorrectArguments() {
        // version space mock
        VersionSpace versionSpace = mock(VersionSpace.class);
        when(versionSpace.sample(any(), anyInt())).thenReturn(mock(Classifier.class));

        // majority vote learner
        int sampleSize = 5;
        learner = new MajorityVoteLearner(versionSpace, sampleSize);

        // fit and verify
        LabeledDataset labeledDataset = mock(LabeledDataset.class);
        learner.fit(labeledDataset);
        verify(versionSpace, times(1)).sample(any(), eq(sampleSize));
    }
}