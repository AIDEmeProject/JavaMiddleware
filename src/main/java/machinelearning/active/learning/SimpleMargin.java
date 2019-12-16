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

package machinelearning.active.learning;

import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.MarginRanker;
import machinelearning.classifier.svm.SvmLearner;

/**
 * Simple Margin is a Active Learning technique introduced in [1]. It approximates a version space cutting technique
 * by relying on properties of the SVM classifier. However, the algorithm can be stated in very simple terms: retrieve
 * at every iteration the point closest to the SVM's current decision boundary.
 *
 * References:
 *  [1] Support Vector Machine Active Learning with Applications to Text Classification
 *      Simon Tong, Daphne Koller
 *      Journal of Machine Learning Research, 2001
 *
 * @see <a href="http://www.jmlr.org/papers/volume2/tong01a/tong01a.pdf">Original paper</a>
 */
public class SimpleMargin implements ActiveLearner {
    /**
     * SVM classifier
     */
    private SvmLearner svmLearner;

    public SimpleMargin(SvmLearner svmLearner) {
        this.svmLearner = svmLearner;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        return new MarginRanker(svmLearner.fit(labeledPoints));
    }
}
