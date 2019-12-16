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

package utils.linalg;

import utils.Validator;

import java.util.Arrays;

public class Shape {
    private final int[] dimensions;
    private final int[] capacities;

    public Shape(int... dimensions) {
        this.dimensions = validateDimensions(dimensions);
        this.capacities = computeCapacity();
    }

    private int[] validateDimensions(int[] dimensions) {
        Validator.assertNotEmpty(dimensions);
        for (int dim : dimensions) {
            if (dim <= 0) {
                throw new IllegalArgumentException();
            }
        }
        return dimensions;
    }

    private int[] computeCapacity() {
        int[] capacities = new int[dimensions.length + 1];

        capacities[capacities.length - 1] = 1;
        for (int i = dimensions.length - 1; i >= 0 ; i--) {
            capacities[i] = capacities[i + 1] * dimensions[i];
        }

        return capacities;
    }

    public int getCapacity() {
        return capacities[0];
    }

    public int get(int index) {
        return dimensions[index];
    }

    public int getPosition(int... indexes) {
        Validator.assertEqualLengths(indexes, dimensions);

        int pos = 0;
        for (int i = 0; i < indexes.length; i++) {
            Validator.assertIndexInBounds(indexes[i], 0, dimensions[i]);
            pos += capacities[i+1] * indexes[i];
        }

        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return Arrays.equals(dimensions, shape.dimensions);
    }

    @Override
    public String toString() {
        return Arrays.toString(dimensions);
    }
}
