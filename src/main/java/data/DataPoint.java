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

import utils.linalg.Vector;

import java.util.Arrays;

/**
 * A DataPoint is an indexed collection of values. More specifically, it is composed of two entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 */
public class DataPoint {

    /**
     * data point's unique id
     */
    private long id;

    /**
     * data point's values
     */
    private Vector data;

    /**
     * @param id: data point's unique ID
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(long id, Vector data) {
        this.id = id;
        this.data = data;
    }

    public DataPoint(long id, double[] data) {
        this.id = id;
        this.data = Vector.FACTORY.make(data);
    }

    public long getId() {
        return id;
    }

    public Vector getData() {
        return data;
    }

    public double get(int i){
        return data.get(i);
    }

    /**
     * @return data point's dimension (i.e. number of features)
     */
    public int getDim(){
        return data.dim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;
        return id == dataPoint.id && data.equals(dataPoint.data);
    }

    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public DataPoint getSelectedAttributes(int[] indices) {
        Arrays.sort(indices);
        return new DataPoint(id, data.select(indices));
    }

    @Override
    public String toString() {
        return "{\"id\": " + getId()  + ", \"data\": " + data + '}';
    }
}
