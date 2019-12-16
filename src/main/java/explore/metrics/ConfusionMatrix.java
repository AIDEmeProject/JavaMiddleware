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

import java.util.HashMap;
import java.util.Map;

/**
 * This module is responsible for storing the confusion matrix values. In particular, we provide methods for computing
 * other classification metrics  like Precision, Recall, Accuracy, and F-Score.
 *
 * We only support the binary classification (0 and 1 labels).
 * @see <a href="https://en.wikipedia.org/wiki/Confusion_matrix">Confusion Matrix Wiki</a>
 * @see ConfusionMatrixCalculator
 * @author luciano
 */
public class ConfusionMatrix implements MetricStorage {
    private final int truePositives;
    private final int trueNegatives;
    private final int falsePositives;
    private final int falseNegatives;

    public ConfusionMatrix(int truePositives, int trueNegatives, int falsePositives, int falseNegatives) {
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
    }

    /**
     * @return true positives (prediction = label = 1)
     */
    public int truePositives() {
        return truePositives;
    }

    /**
     * @return true negatives (prediction = label = 0)
     */
    public int trueNegatives() {
        return trueNegatives;
    }

    /**
     * @return false positives (prediction = 1, label = 0)
     */
    public int falsePositives() {
        return falsePositives;
    }

    /**
     * @return false negatives (prediction = 0, label = 1)
     */
    public int falseNegatives() {
        return falseNegatives;
    }

    /**
     * @return classification accuracy ( # correct predictions / # total )
     */
    public double accuracy(){
        return trueDivide(truePositives + trueNegatives, truePositives + trueNegatives + falsePositives + falseNegatives);
    }

    /**
     * @return classification precision (TP / TP + FP)
     */
    public double precision(){
        return trueDivide(truePositives, truePositives + falsePositives);
    }

    /**
     * @return classification precision (TP / TP + FN)
     */
    public double recall(){
        return trueDivide(truePositives, truePositives + falseNegatives);
    }

    /**
     * @return classification F-score (harmonic mean of precision and recall)
     */
    public double fscore(){
        return trueDivide(2*truePositives, 2*truePositives + falsePositives + falseNegatives);
    }

    private double trueDivide(double a, double b){
        return (b == 0) ? 0 : a / b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfusionMatrix that = (ConfusionMatrix) o;
        return truePositives == that.truePositives &&
                trueNegatives == that.trueNegatives &&
                falsePositives == that.falsePositives &&
                falseNegatives == that.falseNegatives;
    }

    /**
     * @return Map object containing all metrics  stored in the ConfusionMatrix object.
     */
    public Map<String, Double> getMetrics(){
        HashMap<String, Double> metrics = new HashMap<>();
        metrics.put("TruePositives", (double) truePositives());
        metrics.put("TrueNegatives", (double) trueNegatives());
        metrics.put("FalsePositives", (double) falsePositives());
        metrics.put("FalseNegatives", (double) falseNegatives());
        metrics.put("Precision", precision());
        metrics.put("Recall", recall());
        metrics.put("Fscore", fscore());
        return metrics;
    }
}
