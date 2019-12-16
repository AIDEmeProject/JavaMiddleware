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

package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.classifier.margin.HyperPlane;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

public class EuclideanEllipsoid implements Ellipsoid {
    private final Vector center;
    private final Matrix scale;

    /**
     * LDL^T decomposition of matrix
     */
    private final Matrix L;
    private final Vector D;

    public EuclideanEllipsoid(int dim, double radius) {
        Validator.assertPositive(dim);
        Validator.assertPositive(radius);

        this.center = Vector.FACTORY.zeros(dim);
        this.scale = Matrix.FACTORY.identity(dim).fillDiagonal(1.0 / radius);
        this.L = Matrix.FACTORY.identity(dim);
        this.D = Vector.FACTORY.fill(dim, 1.0 / radius);
    }

    public EuclideanEllipsoid(Vector center, Matrix scale, Matrix l, Vector d) {
        this.center = center;
        this.scale = scale;
        L = l;
        D = d;
    }

    public int dim() {
        return center.dim();
    }

    public Vector getCenter() {
        return center;
    }

    public Matrix getScale() {
        return scale;
    }

    public Matrix getL() {
        return L;
    }

    public Vector getD() {
        return D;
    }

    public Matrix getCholeskyFactor() {
        return L.multiplyRow(D.applyMap(Math::sqrt));
    }

    /**
     * Computes the minimum volume ellipsoid containing the intersection of {@code this} and the negative-space defined
     * by the hyperplane.
     *
     * @param hyperplane: cutting hyperplane
     * @return whether ellipsoid changed
     * @throws RuntimeException if the hyperplane does not intersect the
     */
    public boolean cut(HyperPlane hyperplane) {
        int n = dim();
        Vector g = hyperplane.getWeights();

        Vector aHat = L.transpose().multiply(g);
        double gamma = Math.sqrt(aHat.multiply(aHat).dot(D));
        double alpha = hyperplane.margin(center) / gamma;

        if (alpha >= 1) {
            throw new RuntimeException("Invalid hyperplane: ellipsoid is contained in its positive semi-space (expected the negative one)");
        }

        // shallow cut: ellipsoid does not change
        if (alpha <= -1.0 / n) {
            return false;
        }

        Vector p = D.multiply(aHat).iScalarDivide(gamma);
        Vector Pg = L.multiply(p);

        // update center
        double tau = (1 + n * alpha) / (n + 1);
        center.iSubtract(Pg.scalarMultiply(tau));

        // update LDL^T
        double sigma = 2 * tau / (alpha + 1);
        double delta = (1 - alpha * alpha) * (n * n / (n * n - 1.));

        Vector beta = updateDiagonal(p, sigma, delta);
        updateCholeskyFactor(p, beta);

        // update P
        scale.iSubtract(Pg.outerProduct(Pg).iScalarMultiply(sigma));
        scale.iScalarMultiply(delta);

        return true;
    }

    private Vector updateDiagonal(Vector p, double sigma, double delta) {
        int n = dim();

        Vector beta = Vector.FACTORY.zeros(n);
        double tI, tNext = 1 - sigma;

        for (int i = n - 1; i >= 0; i--) {
            double pI = p.get(i), dI = D.get(i);

            tI = tNext + sigma * pI * pI / dI;
            D.set(i, dI * tNext / tI);
            beta.set(i, -sigma * pI / (dI * tNext));

            tNext = tI;
        }

        D.iScalarMultiply(delta);

        return beta;
    }

    private void updateCholeskyFactor(Vector p, Vector beta) {
        int n = dim();

        for (int i = 1; i < n; i++) {
            double v = p.get(i);

            for (int j = i - 1; j >= 0; j--) {
                double val = L.get(i, j);
                L.set(i, j, val + beta.get(j) * v);
                v += val * p.get(j);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ellipsoid)) return false;
        Ellipsoid that = (Ellipsoid) o;
        return Objects.equals(center, that.getCenter()) &&
                Objects.equals(scale, that.getScale()) &&
                Objects.equals(L, that.getL()) &&
                Objects.equals(D, that.getD());
    }
}
