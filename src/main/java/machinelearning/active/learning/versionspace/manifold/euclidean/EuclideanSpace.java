package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import utils.linalg.Vector;

import java.util.Random;

public class EuclideanSpace implements Manifold {

    @Override
    public Geodesic getGeodesic(Vector center, Vector velocity) {
        return new Line(center, velocity);
    }

    @Override
    public Vector sampleVelocity(Vector point, Random random) {
        Vector direction = Vector.FACTORY.zeroslike(point);

        for (int i = 0; i < direction.dim(); i++) {
            direction.set(i, random.nextGaussian());
        }

        return direction;
    }
}
