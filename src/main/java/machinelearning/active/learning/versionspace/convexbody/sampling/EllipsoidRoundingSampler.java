package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import org.apache.commons.math3.linear.*;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;


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
public class EllipsoidRoundingSampler implements DirectionSamplingStrategy {
    /**
     * Ellipsoid's center
     */
    private RealVector center;

    /**
     * Ellipsoid's matrix
     */
    private RealMatrix matrix;

    /**
     * Unit Ball sampler
     */
    private UnitSphereSampler sampler = new UnitSphereSampler();

    /**
     * @param rand: random number generator
     * @return the fitted matrix multiplied by
     */
    @Override
    public double[] sampleDirection(Random rand) {
        return matrix.operate(sampler.sampleDirection(rand));
    }

    @Override
    public void fit(ConvexBody body) {
        sampler.fit(body);

        fitWeakLownerJohnEllipsoid(body);
        matrix = new CholeskyDecomposition(matrix).getL();
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
                Optional<double[]> separatingHyperplane = body.getSeparatingHyperplane(iterator.next());

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
        center = new ArrayRealVector(body.getDim());
        matrix = MatrixUtils.createRealIdentityMatrix(body.getDim());
    }

    /**
     * Let E_k be the current ellipsoid, and let H be a hyperplane cutting E_k. This method will update E_k to be the
     * smallest volume ellipsoid containing E_k intersection with the negative half space of H.
     *
     * For more details, check lemma 2.2.1 from [1].
     */
    private void ellipsoidMethodUpdate(double[] hyperplane) {
        RealVector g = new ArrayRealVector(hyperplane);
        RealVector Pg = matrix.operate(g);
        Pg.mapDivideToSelf(Math.sqrt(Pg.dotProduct(g)));

        int n = center.getDimension();
        center = center.subtract(Pg.mapDivide(n + 1.));
        matrix = matrix.subtract(Pg.outerProduct(Pg).scalarMultiply(2./(n + 1))).scalarMultiply(n*n/(n*n - 1.));
    }

    /**
     * The purpose of this iterator is to go over all the points needing checking of the ellipsoid E_k / (n + 1): its center
     * and all the axis extremes
     */
    private class AxisIterator implements Iterator<double[]> {
        private int counter;
        private EigenDecomposition decomposition;

        public AxisIterator() {
            this.counter = -1;
            this.decomposition = null;
        }

        @Override
        public boolean hasNext() {
            return counter < 2*center.getDimension();
        }

        @Override
        public double[] next() {
            if (counter == -1) {
                counter++;
                return center.toArray();
            }

            if (decomposition == null) {
                this.decomposition = new EigenDecomposition(matrix);
            }

            int sign = counter % 2 == 0 ? 1 : -1;
            int index = counter / 2;
            counter++;

            double eigenvalue = decomposition.getRealEigenvalue(index);
            RealVector ellipsoidSemiAxisDirection = decomposition.getEigenvector(index);
            RealVector scaledAxisDirection = ellipsoidSemiAxisDirection.mapMultiply(sign * Math.sqrt(eigenvalue) / (center.getDimension() + 1.));

            return center.add(scaledAxisDirection).toArray();
        }
    }
}
