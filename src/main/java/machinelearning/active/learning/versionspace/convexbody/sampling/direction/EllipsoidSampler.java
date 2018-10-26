package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import utils.linalg.CholeskyDecomposition;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;

/**
 * This class is responsible for sampling a direction uniformly from the surface of an ellipsoid. An ellipsoid can be
 * defined by the equation:
 *
 *   \( E = \{ x : x^T A^{-1} x = 1 \} \)
 *
 * where the matrix A is assumed to be known. In this case, we can sample a random point on it by the following algorithm:
 *
 *      1) Sampler a vector X uniformly at random from the unit sphere
 *      2) Compute the Cholesky decomposition of A: \( A = L L^T \)  TODO: couldn't we use PD^{1/2} instead, where A = PDP^T
 *      3) Output the vector \( LX \) as the sample
 */
class EllipsoidSampler implements DirectionSampler {
    private Matrix matrix;
    private RandomDirectionSampler randomDirectionSampler;

    /**
     * @param matrix the matrix A in the description.
     * @throws RuntimeException if computing its Cholesky decomposition failed.
     */
    EllipsoidSampler(Matrix matrix) {
        this.matrix = new CholeskyDecomposition(matrix).getL();
        this.randomDirectionSampler = new RandomDirectionSampler(matrix.cols());
    }

    /**
     * @param rand a random number generator instance
     * @return a random vector X such that X / norm(X) is distributed uniformly over the ellipsoid's surface
     */
    @Override
    public Vector sampleDirection(Random rand) {
        return matrix.multiply(randomDirectionSampler.sampleDirection(rand));
    }
}
