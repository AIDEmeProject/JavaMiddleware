package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import utils.linalg.Vector;

import java.util.Random;

public class UnitSphere implements Manifold {

    private static UnitSphere unitSphere = null;

    private UnitSphere() {
    }

    public static UnitSphere getInstance() {
        if (unitSphere == null) {
            unitSphere = new UnitSphere();
        }

        return unitSphere;
    }
    
    @Override
    public Geodesic getGeodesic(Vector center, Vector velocity) {
        return new GreatCircle(center, velocity);
    }

    /**
     * Generates a random tangent vector at the specified point on the unit sphere. This is accomplished by:
     *
     *      1) Generate Z = (Z1, ..., Zd) from the standard normal distribution
     *      2) The vector Z - (Z^T p) p is a random tangent vector at point p
     *
     */
    @Override
    public Vector sampleVelocity(Vector point, Random random) {
        Vector direction = Vector.FACTORY.zeroslike(point);

        for (int i = 0; i < direction.dim(); i++) {
            direction.set(i, random.nextGaussian());
        }

        double prod = direction.dot(point);
        direction.iSubtract(direction.scalarMultiply(prod));

        return direction.iNormalize(1.0);  // TODO: can we avoid normalizing?
    }
}
