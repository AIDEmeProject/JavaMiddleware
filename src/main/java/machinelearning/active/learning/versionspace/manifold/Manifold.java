package machinelearning.active.learning.versionspace.manifold;

import utils.linalg.Vector;

import java.util.Random;

public interface Manifold {
    Geodesic getGeodesic(Vector center, Vector velocity);

    Vector sampleVelocity(Vector point, Random random);

    default Geodesic sampleGeodesic(Vector center, Random random) {
        return getGeodesic(center, sampleVelocity(center, random));
    }
}
