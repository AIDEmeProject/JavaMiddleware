package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.classifier.margin.LinearClassifier;
import utils.linalg.EigenvalueDecomposition;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Iterator;
import java.util.Optional;


/**
 * Rounding is an algorithm for improving the convergence of the Hit-and-Run algorithm. In cases where the convex body K
 * is very "elongated" or "thin" into some directions, the Hit-and-Run samples tend to concentrate over the edges, no exploring
 * sufficiently the center regions. In order to compensate for these cases, the rounding procedure was introduced.
 *
 * Basically, the algorithm attempts to find a weak Lowner-John ellipsoid pair enclosing the convex body. This pair consists of
 * two ellipsoids satisfying:
 *
 *          \( E \subset K \subset (n+1)\sqrt{n} E \)
 *
 * Given such a pair, we can choose a linear transformation taking E into the unit ball in to be our rounding transformation.
 *
 * A more detailed explanation of the algorithm can be found in [1] and [2].
 *
 * References:
 *      [1] An algorithmic theory of numbers, graphs, and convexity
 *          László Lovász
 *          Society for Industrial and Applied Mathematics
 *          See Lemma 2.2.1 and theorem 2.4.1
 *
 *      [2] CHRR: coordinate hit-and-run with rounding for uniform sampling of constraint-based models
 *          Hulda S Haraldsdóttir, Ben Cousins, Ines Thiele, Ronan M.T Fleming, Santosh Vempala
 *          Bioinformatics, Volume 33, Issue 11, 1 June 2017, Pages 1741–1743
 *          See supplementary material
 */
public class RoundingAlgorithm implements DirectionSamplingAlgorithm {
    /**
     * Ellipsoid's center
     */
    private Vector center;

    /**
     * Ellipsoid's scaling matrix
     */
    private Matrix matrix;

    /**
     * LDL^T decomposition of matrix
     */
    private Matrix L;
    private Vector D;

    //TODO: add max iters


    public Vector getCenter() {
        return center;
    }

    public DirectionSampler fit(ConvexBody body) {
        initialize(body);
        fitWeakLownerJohnEllipsoid(body);
        return new EllipsoidSampler(L.multiplyRow(D.applyMap(Math::sqrt)), false);
    }

    /**
     * Initialize center to the zero vector and matrix to the identity matrix
     */
    private void initialize(ConvexBody body) {
        int dim = body.getDim();
        center = Vector.FACTORY.zeros(dim);
        matrix = Matrix.FACTORY.identity(dim);

        L = Matrix.FACTORY.identity(dim);
        D = Vector.FACTORY.fill(dim, 1.0);
    }

    /**
     * The rounding algorithm is described on the algorithm 2.4.1 of [1]. It constructs a sequence of Ellipsoids E_0, E_1, ...
     * satisfying the following properties:
     *
     *      - K is contained in E_k for all k
     *      - E_{k+1} is contained in E_k
     *
     * At every iteration, we check whether  E_k / (n + 1) is contained in K: if true, we have found a weak Lowner-John pair;
     * otherwise, we further restrict the current ellipsoid through the Ellipsoid Method {@link #ellipsoidMethodUpdate}.
     *
     * In particular, we start the algorithm with a large enough ball E_0 = B(0, R) containing K. TODO: add getRadius() to ConvexBody?
     */
    private void fitWeakLownerJohnEllipsoid(ConvexBody body) {
        boolean converged = false;

        while (!converged) {
            converged = true;

            AxisIterator iterator = new AxisIterator();
            while (converged && iterator.hasNext()) {
                Optional<LinearClassifier> separatingHyperplane = body.getSeparatingHyperplane(iterator.next());

                // if a separating hyperplane exists, it means E_k / sqrt(n) (n+1) is not contained in K, and E_k must be updated
                if (separatingHyperplane.isPresent()) {
                    // in case of shallow cut, continue to next axis
                    converged = !ellipsoidMethodUpdate(separatingHyperplane.get());
                }
            }
        }
    }

    /**
     * Let E_k be the current ellipsoid, and let H be a hyperplane cutting E_k. This method will update E_k to be the
     * smallest volume ellipsoid containing E_k intersection with the negative half space of H.
     *
     * In case of a shallow cut, we skip the execution and try the next one.
     *
     * For more details, check lemma 2.2.1 from [1].
     *
     * @return true if cut succeeded (i.e. was not shallow)
     */
    private boolean ellipsoidMethodUpdate(LinearClassifier hyperplane) {
        int n = center.dim();
        Vector g = hyperplane.getWeights();

        Vector aHat = L.transpose().multiply(g);
        double gamma = Math.sqrt(aHat.multiply(aHat).dot(D));
        double alpha = hyperplane.margin(center) / gamma;

        if (alpha >= 1) {
            throw new RuntimeException("Invalid hyperplane: ellipsoid is contained in its positive semi-space (expected the negative one)");
        }
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
        matrix.iSubtract(Pg.outerProduct(Pg).iScalarMultiply(sigma));
        matrix.iScalarMultiply(delta);

        return true;
    }

    private Vector updateDiagonal(Vector p, double sigma, double delta) {
        int n = center.dim();

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
        int n = center.dim();

        for (int i = 1; i < n; i++) {
            double v = p.get(i);

            for (int j = i - 1; j >= 0; j--) {
                double val = L.get(i, j);
                L.set(i, j, val + beta.get(j) * v);
                v += val * p.get(j);
            }
        }
    }

    /**
     * The purpose of this iterator is to go over all the points needing checking of the ellipsoid E_k / (n + 1): its center
     * and all the axis extremes
     */
    private class AxisIterator implements Iterator<Vector> {
        private int counter;
        private EigenvalueDecomposition decomposition;

        public AxisIterator() {
            this.counter = -1;
            this.decomposition = null;
        }

        @Override
        public boolean hasNext() {
            return counter < 2 * center.dim();
        }

        @Override
        public Vector next() {
            if (counter == -1) {
                counter++;
                return center;
            }

            if (decomposition == null) {
                this.decomposition = new EigenvalueDecomposition(matrix);
            }

            int sign = counter % 2 == 0 ? 1 : -1;
            int index = counter / 2;
            counter++;

            double eigenvalue = decomposition.getEigenvalue(index);

            Vector ellipsoidSemiAxisDirection = decomposition.getEigenvector(index);
            Vector scaledAxisDirection = ellipsoidSemiAxisDirection.scalarMultiply(sign * Math.sqrt(eigenvalue) / (center.dim() + 1.));

            return center.add(scaledAxisDirection);
        }
    }
}
