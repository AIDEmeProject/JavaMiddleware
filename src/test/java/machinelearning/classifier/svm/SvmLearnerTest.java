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

import data.LabeledDataset;
import data.LabeledPoint;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.margin.KernelClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SvmLearnerTest {

    private SvmLearner learner;

    @BeforeEach
    void setUp() {
        learner = new SvmLearner(1, new LinearKernel());
    }

    @Test
    void fit_twoPointsTrainingSet_pointsCorrectlyClassifier() {
        LabeledDataset labeledPoints = buildTrainingSet(
                new double[][]{{-1, 0}, {1, 0}}, new Label[]{Label.NEGATIVE, Label.POSITIVE});

        Classifier classifier = learner.fit(labeledPoints);

        for (LabeledPoint point : labeledPoints) {
            assertEquals(point.getLabel(), classifier.predict(point.getData()));
        }
    }

    @Test
    void fit_twoPointsTrainingSet_fittedClassifierHasExpectedMargin() {
        LabeledDataset labeledPoints = buildTrainingSet(
                new double[][]{{-1, 0}, {1, 0}}, new Label[]{Label.NEGATIVE, Label.POSITIVE});

        KernelClassifier classifier = (KernelClassifier) learner.fit(labeledPoints);

        for (LabeledPoint point : labeledPoints) {
            assertEquals(point.getLabel().asSign() * 1.0, classifier.margin(point.getData()));
        }
    }

    private LabeledDataset buildTrainingSet(double[][] x, Label[] y) {
        List<Long> indexes = new ArrayList<>();

        for (long i = 0; i < x.length; i++) {
            indexes.add(i);
        }

        return new LabeledDataset(indexes, Matrix.FACTORY.make(x), y);
    }
}