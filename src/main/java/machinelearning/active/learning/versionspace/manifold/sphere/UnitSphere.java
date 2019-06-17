package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import utils.linalg.Vector;

import java.util.Random;

public class UnitSphere implements Manifold {

    @Override
    public Geodesic getGeodesic(Vector center, Vector velocity) {
        return new GreatCircle(center, velocity);
    }

    /**
     * This class is responsible for generating random vectors X whose direction is uniformly distributed over the unit sphere.
     * In order to do this, we employ the well-known Marsaglia method:
     *
     *      1) Generate X1, ..., Xd from the standard normal distribution
     *      2) The vector (X1, ..., Xd) / norm((X1, ..., Xd)) is uniformly distributed over the sphere
     *
     * In this particular implementation we do not bother with the normalizing step 2, since it not relevant our applications.
     */
    @Override
    public Vector sampleVelocity(Vector point, Random random) {
        Vector direction = Vector.FACTORY.zeroslike(point);

        for (int i = 0; i < direction.dim(); i++) {
            direction.set(i, random.nextGaussian());
        }

        double prod = direction.dot(point);
        direction.iSubtract(direction.scalarMultiply(prod));

        return direction.iNormalize(1.0);
    }
}
