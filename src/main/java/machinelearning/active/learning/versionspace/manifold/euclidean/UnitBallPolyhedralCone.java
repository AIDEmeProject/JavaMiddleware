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

package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.classifier.margin.HyperPlane;
import utils.SecondDegreeEquationSolver;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.LinearProgramSolver;

import java.util.Objects;

public class UnitBallPolyhedralCone implements EuclideanConvexBody {
    private PolyhedralCone cone;

    public UnitBallPolyhedralCone(PolyhedralCone cone) {
        this.cone = cone;
    }

    public UnitBallPolyhedralCone(Matrix A, LinearProgramSolver.FACTORY solver) {
        this.cone = new PolyhedralCone(A, solver);
    }

    @Override
    public int dim() {
        return cone.dim();
    }

    @Override
    public boolean isInside(Vector point) {
        return point.squaredNorm() < 1 && cone.isInside(point);
    }

    @Override
    public Vector getInteriorPoint() {
        return cone.getInteriorPoint().iNormalize(0.9);
    }

    @Override
    public GeodesicSegment computeIntersection(Geodesic line) {
        double a = line.getVelocity().squaredNorm();
        double b = line.getCenter().dot(line.getVelocity());
        double c = line.getCenter().squaredNorm() - 1;
        SecondDegreeEquationSolver.SecondDegreeEquationSolution solution = SecondDegreeEquationSolver.solve(a, b, c);

        return cone.computeIntersection(line, solution.getFirst(), solution.getSecond());
    }

    @Override
    public Manifold getManifold() {
        return cone.getManifold();
    }

    @Override
    public double getRadius() {
        return 1.0;
    }

    /**
     * For a exterior point x, we compute an separating hyperplane by using the following rule:
     *
     *      - if point is outside the unit ball, just return x itself
     *      - else, we look for a any violated linear constraint
     *
     * @param x: a data point
     * @return the separating hyperplane
     */
    @Override
    public HyperPlane getSeparatingHyperplane(Vector x) {
        Validator.assertEquals(x.dim(), dim());

        if (x.squaredNorm() >= 1) {
            return new HyperPlane(-1, x.normalize(1.0));
        }

        return cone.getSeparatingHyperplane(x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitBallPolyhedralCone that = (UnitBallPolyhedralCone) o;
        return Objects.equals(cone, that.cone);
    }
}
