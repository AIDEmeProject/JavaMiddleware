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

import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LabeledSetConfusionMatrixCalculatorTest {
    private PartitionedDataset data;
    private LabeledSetConfusionMatrixCalculator calculator;
    private User user;

    private Classifier classifierStub;
    private Learner learnerStub;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(-2L, new double[]{-2});
        builder.add(-1L, new double[]{-1});
        builder.add(0L, new double[]{0});
        builder.add(1L, new double[]{1});
        builder.add(2L, new double[]{2});

        data = new PartitionedDataset(builder.build());
        user = mock(User.class);

        // returns mock learner and classifier
        classifierStub = spy(Classifier.class);
        learnerStub = mock(Learner.class);
        when(learnerStub.fit(any())).thenReturn(classifierStub);

        // calculator
        calculator = new LabeledSetConfusionMatrixCalculator(learnerStub);
    }

    @Test
    void compute_emptyLabeledSet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(data, user));
    }

    @Test
    void compute_nonEmptyLabeledSet_userNeverCalled() {
        data.update(new LabeledPoint(-2L, new double[] {-2}, Label.NEGATIVE));
        calculator.compute(data, user);
        verify(user, never()).getLabel((DataPoint) any());
    }

    @Test
    void compute_mockedLearner_calledOnceOverTheLabeledSet() {
        data.update(new LabeledPoint(-2L, new double[] {-2}, Label.NEGATIVE));
        calculator.compute(data, user);
        verify(learnerStub).fit(data.getLabeledPoints());
    }

    @Test
    void compute_mockedClassifier_predictCalledOverLabeledSet() {
        data.update(new LabeledPoint(-2L, new double[] {-2}, Label.NEGATIVE));
        calculator.compute(data, user);
        verify(classifierStub).predict(data.getLabeledPoints().getData());
    }
}