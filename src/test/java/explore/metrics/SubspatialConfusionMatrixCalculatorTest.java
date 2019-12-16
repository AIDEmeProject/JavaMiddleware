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

package explore.metrics;

import data.IndexedDataset;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.user.FactoredUser;
import explore.user.User;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import machinelearning.classifier.SubspatialLearner;
import machinelearning.classifier.SubspatialWorker;
import machinelearning.threesetmetric.LabelGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class SubspatialConfusionMatrixCalculatorTest {
    private IndexedDataset dataset;
    private PartitionedDataset data;
    private User user;

    private SubspatialLearner learner;
    private SubspatialConfusionMatrixCalculator calculator;

    @BeforeEach
    void setUp() {
        dataset = new IndexedDataset(Arrays.asList(0L, 1L, 2L), Matrix.FACTORY.make(3, 2, 1, 2, 3, 4, 5, 6));
        dataset.setFactorizationStructure(new int[][] {{0}, {1}});

        data = new PartitionedDataset(dataset);
        data.update(new LabeledPoint(0, Vector.FACTORY.make(1, 2), new LabelGroup(Label.POSITIVE, Label.POSITIVE)));

        user = new FactoredUser(Arrays.asList(buildSet(0, 1), buildSet(1, 2)));

        learner = new SubspatialLearner(new Learner[] {mock(Learner.class), mock(Learner.class)}, mock(SubspatialWorker.class));
        calculator = new SubspatialConfusionMatrixCalculator(learner);
    }

    private Set<Long> buildSet(long... values) {
        Set<Long> set = new HashSet<>();
        for (long value : values)
            set.add(value);
        return set;
    }


    @Test
    void compute_dataAndLearnerHaveIncompatibleNumberOfPartitions_throwsException() {
        Learner lr = mock(Learner.class);
        learner = new SubspatialLearner(new Learner[]{lr}, mock(SubspatialWorker.class));
        calculator = new SubspatialConfusionMatrixCalculator(learner);
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(data, user));
    }

    @Test
    void compute_dataAndUserHaveIncompatibleNumberOfPartitions_throwsException() {
        user = new FactoredUser(Arrays.asList(buildSet(1L)));
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(data, user));
    }
}