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

import java.util.ArrayList;
import java.util.List;

public class IncrementalCholesky {
    private int currentDim = 0;
    private final ArrayList<Vector> choleskyRows, inverseRows;

    public IncrementalCholesky() {
        choleskyRows = new ArrayList<>();
        inverseRows = new ArrayList<>();
    }

    public int getCurrentDim() {
        return currentDim;
    }

    public void increment(double... values) {
        increment(Vector.FACTORY.make(values));
    }

    public void increment(Vector point) {
        Validator.assertEquals(point.dim(), currentDim + 1);

        updateCholesky(point);
        updateInverse();

        currentDim++;
    }

    private void updateCholesky(Vector point) {
        Vector row = Vector.FACTORY.zeroslike(point);

        int col = 0;
        for (Vector cholRow: choleskyRows) {
            double sum = point.get(col);

            for (int k = 0; k < col; k++) {
                sum -= row.get(k) * cholRow.get(k);
            }

            row.set(col, sum / cholRow.get(col));

            col++;
        }

        double value = point.get(col) - row.squaredNorm();

        if (value <= 0) {
            throw new RuntimeException("Matrix is not positive definite: value = " + value);
        }

        row.set(col, Math.sqrt(value));
        choleskyRows.add(row);
    }

    private void updateInverse() {
        Vector row = Vector.FACTORY.zeros(currentDim + 1);

        Vector chol = choleskyRows.get(currentDim);
        double scale = 1.0 / chol.get(currentDim);
        row.set(currentDim, scale);

        for (int j = 0; j < currentDim; j++) {
            double sum = 0;

            for (int r = j; r < currentDim; r++) {
                sum += chol.get(r) * inverseRows.get(r).get(j);
            }

            row.set(j, -sum * scale);
        }

        inverseRows.add(row);
    }


    public Matrix getL() {
        return parseList(choleskyRows);
    }

    public Matrix getInverse() {
        return parseList(inverseRows);
    }

    private Matrix parseList(List<Vector> list) {
        Validator.assertPositive(currentDim);

        Matrix mat = Matrix.FACTORY.zeros(currentDim, currentDim);

        for (int i = 0; i < currentDim; i++) {
            for (int j = 0; j <= i; j++) {
                mat.set(i, j, list.get(i).get(j));
            }
        }

        return mat;
    }

}
