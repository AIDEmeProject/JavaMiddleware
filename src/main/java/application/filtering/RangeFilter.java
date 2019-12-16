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

package application.filtering;

import java.util.StringJoiner;

/**
 * Filters for numerical attributes. They represents filters on the form:
 *                          min <= column <= max
 */
public class RangeFilter implements Filter {
    private final String columnName;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;

    public RangeFilter(String columnName) {
        this.columnName = columnName;
    }

    public RangeFilter(String columnName, double min, double max) {
        this(columnName);
        setMin(min);
        setMax(max);
    }

    public void setMin(double min) {
        if (min > this.max) {
            throw new IllegalArgumentException("Minimum cannot be larger than maximum.");
        }
        this.min = min;
    }

    public void setMax(double max) {
        if (max < this.min) {
            throw new IllegalArgumentException("Maximum cannot be smaller than minimum.");
        }
        this.max = max;
    }

    @Override
    public String buildPredicate() {
        StringJoiner joiner = new StringJoiner(" AND ", "(", ")");

        if (Double.isFinite(min)) {
            joiner.add(columnName + " >= " + min);
        }

        if (Double.isFinite(max)) {
            joiner.add(columnName + " <= " + max);
        }

        return joiner.toString();
    }

    @Override
    public String toString() {
        return "RangeFilter{" +
                "columnName='" + columnName + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
