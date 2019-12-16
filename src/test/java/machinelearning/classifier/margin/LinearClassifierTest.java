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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearClassifierTest {
    private LinearClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new LinearClassifier(1, Vector.FACTORY.make(-1,2));
    }

    @Test
    void margin_incompatibleDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> classifier.margin(Vector.FACTORY.zeros(3)));
    }

    @Test
    void margin_pointOnBoundary_returnsZero() {
        Vector point = Vector.FACTORY.make(-1, -1);
        assertEquals(0, classifier.margin(point));
    }

    @Test
    void margin_pointOnPositiveSideOfMargin_returnsCorrectMargin() {
        Vector point = Vector.FACTORY.make(-1, 3);
        assertEquals(8, classifier.margin(point));
    }

    @Test
    void margin_pointOnNegativeSideOfMargin_returnsCorrectMargin() {
        Vector point = Vector.FACTORY.make(1, -3);
        assertEquals(-6, classifier.margin(point));
    }

    @Test
    void predict_incompatibleDimension_throwsException() {
        Vector point = Vector.FACTORY.zeros(3);
        assertThrows(RuntimeException.class, () -> classifier.predict(point));
    }

    @Test
    void probability_incompatibleDimension_throwsException() {
        Vector point = Vector.FACTORY.zeros(3);
        assertThrows(RuntimeException.class, () -> classifier.probability(point));
    }
}