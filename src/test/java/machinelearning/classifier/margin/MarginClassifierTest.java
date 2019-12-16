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

import machinelearning.classifier.Label;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MarginClassifierTest {
    private MarginClassifier classifier;
    
    @Test
    void predict_positiveMargin_returnPositiveLabel() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(1.);
        assertEquals(Label.POSITIVE, classifier.predict(mock(Vector.class)));
    }

    @Test
    void predict_negativeMargin_returnNegativeLabel() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(-1.);
        assertEquals(Label.NEGATIVE, classifier.predict(mock(Vector.class)));
    }

    @Test
    void predict_zeroMargin_returnNegativeLabel() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(0.);
        assertEquals(Label.NEGATIVE, classifier.predict(mock(Vector.class)));
    }

    @Test
    void probability_positiveMargin_returnExpectedProbability() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(1.);
        assertEquals(0.731058579, classifier.probability(mock(Vector.class)), 1e-8);
    }

    @Test
    void probability_negativeMargin_returnExpectedProbability() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(-1.);
        assertEquals(0.268941421, classifier.probability(mock(Vector.class)), 1e-8);
    }

    @Test
    void probability_zeroMargin_returnOneHalf() {
        classifier = mock(MarginClassifier.class, CALLS_REAL_METHODS);
        when(classifier.margin((Vector) any())).thenReturn(0.);
        assertEquals(0.5, classifier.probability(mock(Vector.class)), 1e-8);
    }
}