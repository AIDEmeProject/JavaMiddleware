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

import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class ConfusionMatrixCalculatorTest {

    private ConfusionMatrixCalculator calculator = new ConfusionMatrixCalculator(mock(Learner.class));

    @Test
    void compute_ZeroLengthLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.compute(new Label[0], new Label[0]));
    }

    @Test
    void compute_IncompatibleSizesLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->  calculator.compute(new Label[2], new Label[4]));
    }

    @Test
    void compute_allLabelsCorrectlyPredicted_confusionMatrixCorrectlyComputed() {
        Label[] labels = {Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE};
        ConfusionMatrix metric = calculator.compute(labels, labels);
        assertEquals(2, metric.truePositives());
        assertEquals(3, metric.trueNegatives());
        assertEquals(0, metric.falsePositives());
        assertEquals(0, metric.falseNegatives());
    }

    @Test
    void compute_allLabelsWronglyPredicted_confusionMatrixCorrectlyComputed() {
        Label[] predictedLabels = {Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE};
        Label[] trueLabels = {Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE};
        ConfusionMatrix metric = calculator.compute(trueLabels, predictedLabels);
        assertEquals(0, metric.truePositives());
        assertEquals(0, metric.trueNegatives());
        assertEquals(3, metric.falsePositives());
        assertEquals(2, metric.falseNegatives());
    }

    @Test
    void compute_predictedLabelsPartiallyCorrect_confusionMatrixCorrectlyComputed() {
        Label[] predictedLabels = {Label.NEGATIVE, Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE};
        Label[] trueLabels = {Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE, Label.NEGATIVE};
        ConfusionMatrix metric = calculator.compute(trueLabels, predictedLabels);
        assertEquals(1, metric.truePositives());
        assertEquals(2, metric.trueNegatives());
        assertEquals(1, metric.falsePositives());
        assertEquals(1, metric.falseNegatives());
    }
}
