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

package machinelearning.classifier;

import data.LabeledDataset;
import explore.user.UserLabel;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a learner for categorical features. It assumes that the input matrix is the one-hot encoding of
 * a categorical feature, and simply memorizes which labels are positive or negative.
 */
public class CategoricalLearner implements Learner {
    @Override
    public CategoricalClassifier fit(LabeledDataset labeledPoints) {
        Matrix data = labeledPoints.getData();
        UserLabel[] labels = labeledPoints.getLabels();

        Set<Integer> positive = new HashSet<>();
        Set<Integer> negative = new HashSet<>();

        for (int i = 0; i < labels.length; i++) {
            int index = findCategoryIndex(data.getRow(i));
            if (labels[i].isPositive()) {
                positive.add(index);
            }
            else {
                negative.add(index);
            }
        }

        return new CategoricalClassifier(positive, negative);
    }

    static int findCategoryIndex(Vector vector) {
        for (int i = 0; i < vector.dim(); i++) {
            if (vector.get(i) > 0)
                return i;
        }
        throw new RuntimeException("All values are negative!");
    }
}
