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

package explore.user;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.classifier.Label;
import utils.Validator;

/**
 * An User represents the "oracle" of Active Learning scenario, i.e. a human annotator capable of, given a {@link DataPoint},
 * return whether it is represents a POSITIVE or a NEGATIVE {@link Label}.
 */
public interface User {
    /**
     * Given a dataset and a row, return the label of data[row]
     * @param point: point to label
     * @return label of data[row]
     */
    UserLabel getLabel(DataPoint point);

    /**
     * Return the labels of a batch of rows
     * @param points: collection of data points
     * @return an array containing the labels of each requested row
     */
    default UserLabel[] getLabel(IndexedDataset points){
        return points.stream()
                .map(this::getLabel)
                .toArray(UserLabel[]::new);
    }

    default Label[][] getPartialLabels(IndexedDataset dataset) {
        UserLabel[] labels = getLabel(dataset);

        Validator.assertEquals(dataset.partitionSize(), labels[0].getLabelsForEachSubspace().length);

        Label[][] partialLabels = new Label[dataset.partitionSize()][dataset.length()];
        for (int i = 0; i < partialLabels.length; i++) {
            for (int j = 0; j < dataset.length(); j++) {
                partialLabels[i][j] = labels[j].getLabelsForEachSubspace()[i];
            }
        }
        return partialLabels;
    }
}
