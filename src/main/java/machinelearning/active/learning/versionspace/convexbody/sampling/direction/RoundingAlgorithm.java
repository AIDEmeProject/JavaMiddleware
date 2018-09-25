package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
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
     * Ellipsoid's matrix
     */
    private Matrix matrix;

    @Override
    public DirectionSampler fit(ConvexBody body) {
        fitWeakLownerJohnEllipsoid(body);
        return new EllipsoidSampler(matrix);
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
        initialize(body);

        boolean converged = false;

        while (!converged) {
            converged = true;

            AxisIterator iterator = new AxisIterator();
            while (converged && iterator.hasNext()) {
                Optional<Vector> separatingHyperplane = body.getSeparatingHyperplane(iterator.next());

                // if a separating hyperplane exists, it means E_k / (n+1) is not contained in K, and E_k must be updated
                if (separatingHyperplane.isPresent()) {
                    converged = false;
                    ellipsoidMethodUpdate(separatingHyperplane.get());
                }
            }
        }
    }

    /**
     * Initialize center to the zero vector and matrix to the identity matrix
     */
    private void initialize(ConvexBody body) {
        center = Vector.FACTORY.zeros(body.getDim());
        matrix = Matrix.FACTORY.identity(body.getDim());
    }

    /**
     * Let E_k be the current ellipsoid, and let H be a hyperplane cutting E_k. This method will update E_k to be the
     * smallest volume ellipsoid containing E_k intersection with the negative half space of H.
     *
     * For more details, check lemma 2.2.1 from [1].
     */
    private void ellipsoidMethodUpdate(Vector g) {
        Vector Pg = matrix.multiply(g);
        Pg = Pg.divide(Math.sqrt(Pg.dot(g)));

        int n = center.dim();
        center = center.subtract(Pg.divide(n + 1.));
        matrix = matrix.subtract(Pg.outerProduct(Pg).scalarMultiply(2./(n + 1))).scalarMultiply(n*n/(n*n - 1.));
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
            return counter < 2*center.dim();
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
            Vector scaledAxisDirection = ellipsoidSemiAxisDirection.multiply(sign * Math.sqrt(eigenvalue) / (center.dim() + 1.));

            return center.add(scaledAxisDirection);
        }
    }
}
