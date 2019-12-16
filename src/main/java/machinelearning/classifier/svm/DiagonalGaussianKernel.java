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

package machinelearning.classifier.svm;

import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class DiagonalGaussianKernel extends Kernel {
    private final Vector diagonal;
    private final GaussianKernel gaussianKernel = new GaussianKernel(1.0);

    public DiagonalGaussianKernel(Vector diagonal) {
        for (int i = 0; i < diagonal.dim(); i++) {
            Validator.assertPositive(diagonal.get(i));
        }

        this.diagonal = diagonal.applyMap(Math::sqrt);
    }

    @Override
    public double compute(Vector x, Vector y) {
        x = x.multiply(this.diagonal);
        y = y.multiply(this.diagonal);
        return gaussianKernel.compute(x, y);
    }

    @Override
    public Vector compute(Matrix xs, Vector y) {
        xs = xs.multiplyRow(this.diagonal);
        y = y.multiply(this.diagonal);
        return gaussianKernel.compute(xs, y);
    }

    @Override
    public Matrix compute(Matrix xs, Matrix ys) {
        return gaussianKernel.compute(xs.multiplyRow(this.diagonal), ys.multiplyRow(this.diagonal));
    }

    @Override
    public Matrix compute(Matrix xs) {
        return gaussianKernel.compute(xs.multiplyRow(this.diagonal));
    }
}
