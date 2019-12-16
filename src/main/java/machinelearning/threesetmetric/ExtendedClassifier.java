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

package machinelearning.threesetmetric;

import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;

import java.util.Collection;
import java.util.Collections;

/**
 * A ExtendedClassifier is responsible for building an accurate model of the user interest and disinterest regions. This model
 * can be continuously updated as the real user provides more feedback, and for any given point its label can be predicted.
 *
 * In contrast to the usual Machine Learning classifier, a ExtendedClassifier may return one of three possible labels:
 * POSITIVE, NEGATIVE, or UNKNOWN. See {@link ExtendedLabel} for more details.
 */
public interface ExtendedClassifier {

    /**
     * Update the current data model with new labeled data.
     * @param labeledPoint a {@link LabeledPoint} instance
     */
    default void update(LabeledPoint labeledPoint) {
        update(Collections.singleton(labeledPoint));
    }

    /**
     * Update the current data model with new labeled data.
     * @param labeledPoint a {@link LabeledPoint} instance
     */
    void update(Collection<LabeledPoint> labeledPoint);


    /**
     * @param dataPoint: a data point
     * @return the predicted label for input point
     */
    ExtendedLabel predict(DataPoint dataPoint);

    /**
     * @param points: a collection of data point
     * @return the predicted labels for each point in the input collection
     */
    default ExtendedLabel[] predict(IndexedDataset points) {
        return points.stream()
                .map(this::predict)
                .toArray(ExtendedLabel[]::new);
    }


    /**
     * @return true if the data model is still running
     */
    boolean isRunning();


    /**
     * @return true if a relabeling of the INFERRED partition is needed
     */
    boolean triggerRelabeling();
}
