package machinelearning.active.learning.versionspace.manifold.direction;

import machinelearning.active.learning.versionspace.manifold.Manifold;
import utils.linalg.Vector;

import java.util.Objects;
import java.util.Random;

/**
 * This class is a wrapper over the {@link Manifold#sampleVelocity(Vector, Random)} method for sampling random velocities.
 * No further processing is done over this vector.
 */
public class RandomDirectionSampler implements DirectionSampler {
    private final Manifold manifold;

    /**
     * @param manifold: manifold to sample directions from
     */
    public RandomDirectionSampler(Manifold manifold) {
        this.manifold = Objects.requireNonNull(manifold);
    }

    /**
     * @return a random (un-normalized) direction at the given point of the manifold
     */
    @Override
    public Vector sampleDirection(Vector point, Random rand) {
        return manifold.sampleVelocity(point, rand);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomDirectionSampler that = (RandomDirectionSampler) o;
        return Objects.equals(manifold, that.manifold);
    }
}
