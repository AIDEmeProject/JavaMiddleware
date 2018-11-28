package machinelearning.active.learning.versionspace.convexbody.sampling.direction;

import utils.Validator;
import utils.linalg.Vector;

import java.util.Random;

/**
 * This class is responsible for generating random vectors X whose direction is uniformly distributed over the unit sphere.
 * In order to do this, we employ the well-known Marsaglia method:
 *
 *      1) Generate X1, ..., Xd from the standard normal distribution
 *      2) The vector (X1, ..., Xd) / norm((X1, ..., Xd)) is uniformly distributed over the sphere
 *
 * In this particular implementation we do not bother with the normalizing step 2, since it is important for our applications.
 *
 * @see DirectionSampler
 * @see EllipsoidSampler
 */
public class RandomDirectionSampler implements DirectionSampler {
    private int dim;

    /**
     * @param dim: dimension of the output samples
     * @throws IllegalArgumentException if dim is not positive
     */
    public RandomDirectionSampler(int dim) {
        Validator.assertPositive(dim);
        this.dim = dim;
    }

    /**
     * @param rand a random number generator instance
     * @return a random vector X such that X / norm(X) is distributed uniformly over the unit sphere
     */
    @Override
    public Vector sampleDirection(Random rand) {
        double[] direction = new double[dim];

        for (int i = 0; i < dim; i++) {
            direction[i] = rand.nextGaussian();
        }

        return Vector.FACTORY.make(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomDirectionSampler that = (RandomDirectionSampler) o;
        return dim == that.dim;
    }
}
