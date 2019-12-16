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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfusionMatrixTest {
    private ConfusionMatrix confusionMatrix;

    private void setUpACoupleLabelsWrongScenario() {
        confusionMatrix = new ConfusionMatrix(2, 1, 4, 3);
    }

    private void setUpAllLabelsWrongScenario(){
        confusionMatrix = new ConfusionMatrix(0, 0, 2, 2);
    }

    private void setUpAllLabelsCorrectScenario(){
        confusionMatrix = new ConfusionMatrix(2, 2, 0, 0);
    }

    //-------------------------------
    // Confusion Matrix Metrics
    //-------------------------------
    @Test
    void truePositives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(2, confusionMatrix.truePositives());
    }

    @Test
    void trueNegatives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(1, confusionMatrix.trueNegatives());
    }

    @Test
    void falsePositives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(4, confusionMatrix.falsePositives());
    }

    @Test
    void falseNegatives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(3, confusionMatrix.falseNegatives());
    }

    //-------------------------------
    // Accuracy
    //-------------------------------
    @Test
    void accuracy_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(0.3, confusionMatrix.accuracy());
    }

    @Test
    void accuracy_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.accuracy());
    }

    @Test
    void accuracy_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.accuracy());
    }

    //-------------------------------
    // Precision
    //-------------------------------
    @Test
    void precision_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(1.0/3, confusionMatrix.precision());
    }

    @Test
    void precision_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.precision());
    }

    @Test
    void precision_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.precision());
    }

    //-------------------------------
    // Recall
    //-------------------------------
    @Test
    void recall_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(0.4, confusionMatrix.recall());
    }

    @Test
    void recall_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.recall());
    }

    @Test
    void recall_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.recall());
    }

    //-------------------------------
    // F-Score
    //-------------------------------
    @Test
    void fscore_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(2/5.5, confusionMatrix.fscore());
    }

    @Test
    void fscore_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.fscore());
    }

    @Test
    void fscore_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.fscore());
    }
}