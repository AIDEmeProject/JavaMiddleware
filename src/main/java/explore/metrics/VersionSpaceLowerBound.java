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

import utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class VersionSpaceLowerBound implements MetricStorage {
    private final int positiveCount, negativeCount, predictedPositives, total;

    public VersionSpaceLowerBound(int positiveCount, int negativeCount, int predictedPositives, int total) {
        Validator.assertNonNegative(positiveCount);
        Validator.assertNonNegative(negativeCount);
        Validator.assertNonNegative(predictedPositives);
        Validator.assertPositive(total);

        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.predictedPositives = predictedPositives;
        this.total = total;
    }

    @Override
    public Map<String, Double> getMetrics() {
        double precisionEstimate = (double) positiveCount / predictedPositives;
        double recallEstimate = (double) positiveCount / (total - negativeCount);
        double lowerBound = precisionEstimate * recallEstimate / (precisionEstimate + recallEstimate);

        System.out.println("lower bound: " + lowerBound);

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("PositiveCount", (double) positiveCount);
        metrics.put("NegativeCount", (double) negativeCount);
        metrics.put("PredictedPositives", (double) predictedPositives);
        metrics.put("PrecisionEstimate", precisionEstimate);
        metrics.put("RecallEstimate", recallEstimate);
        metrics.put("VersionSpaceLowerBound", lowerBound);
        return metrics;
    }
}
