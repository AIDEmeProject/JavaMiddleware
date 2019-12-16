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

package data;

import explore.user.UserLabel;
import machinelearning.classifier.Label;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * A LabeledPoint is a {@link DataPoint} instance containing a {@link UserLabel}. More specifically, it is composed of three entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 *   - label: a {@link UserLabel} (POSITIVE, NEGATIVE, ...)
 */
public class LabeledPoint {
    /**
     * Original data point
     */
    private DataPoint dataPoint;

    /**
     * User label
     */
    private UserLabel label;

    /**
     * @param point: a data point
     * @param label: user label
     * @throws NullPointerException if label is {@code null}
     */
    public LabeledPoint(DataPoint point, UserLabel label) {
        this.dataPoint = point;
        this.label = Objects.requireNonNull(label);
    }

    /**
     * @param id: data point's id
     * @param data: a data vector
     * @param label: user label
     * @throws NullPointerException if label is {@code null}
     */
    public LabeledPoint(long id, Vector data, UserLabel label) {
        this(new DataPoint(id, data), label);
    }

    public LabeledPoint(long id, double[] data, UserLabel label) {
        this(new DataPoint(id, data), label);
    }

    public long getId() {
        return dataPoint.getId();
    }

    public Vector getData() {
        return dataPoint.getData();
    }

    public UserLabel getLabel() {
        return label;
    }

    public int getDim() {
        return dataPoint.getDim();
    }

    public double get(int index) {
        return dataPoint.get(index);
    }

    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public LabeledPoint getSelectedAttributes(int[] indices, Label label) {
        return new LabeledPoint(dataPoint.getSelectedAttributes(indices), label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabeledPoint that = (LabeledPoint) o;
        return Objects.equals(dataPoint, that.dataPoint) &&
                Objects.equals(label, that.label);
    }

    /**
     * @return JSON encoding of this object
     */
    @Override
    public String toString() {
        return "{\"id\": " + dataPoint.getId()  + ", \"data\": " + dataPoint.getData() + ", \"label\": " + label + "}";
    }
}
