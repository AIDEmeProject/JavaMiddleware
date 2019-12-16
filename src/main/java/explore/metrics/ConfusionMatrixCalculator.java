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

import data.PartitionedDataset;
import explore.user.User;
import explore.user.UserLabel;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import utils.Validator;

/**
 * This module is a factory for ConfusionMatrix objects.
 *
 * @see ConfusionMatrix
 */
public class ConfusionMatrixCalculator implements MetricCalculator {
    protected Learner learner;

    public ConfusionMatrixCalculator(Learner learner) {
        this.learner = learner;
    }

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        UserLabel[] trueLabels = user.getLabel(data.getAllPoints());
        Classifier classifier = learner.fit(data.getLabeledPoints());
        return compute(trueLabels, data.predictLabels(classifier));
    }

    /**
     * Computes a ConfusionMatrix from the true labels and predicted labels arrays.
     *
     * @param trueLabels: array of true labels
     * @param predictedLabels: array of predicted labels
     * @return a confusion matrix
     * @throws IllegalArgumentException if inputs have incompatible dimensions or are 0-length arrays
     */
    public static ConfusionMatrix compute(UserLabel[] trueLabels, UserLabel[] predictedLabels){
        Validator.assertEqualLengths(trueLabels, predictedLabels);
        Validator.assertNotEmpty(trueLabels);

        int truePositives = 0, trueNegatives = 0, falseNegatives = 0, falsePositives = 0;

        for(int i=0; i < trueLabels.length; i++){
            if(predictedLabels[i].isPositive()){
                if(trueLabels[i].isPositive())
                    truePositives++;
                else
                    falsePositives++;
            }
            else {
                if(trueLabels[i].isPositive())
                    falseNegatives++;
                else
                    trueNegatives++;
            }
        }

        return new ConfusionMatrix(truePositives, trueNegatives, falsePositives, falseNegatives);
    }
}
