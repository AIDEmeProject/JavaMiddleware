package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;

public class EllipsoidSampler implements DirectionSampler {
    private final Ellipsoid ellipsoid;
    private final Matrix matrix;
    private final Manifold manifold;

    public EllipsoidSampler(Ellipsoid ellipsoid, Manifold manifold) {
        this.ellipsoid = ellipsoid;
        this.matrix = ellipsoid.getCholeskyFactor();
        this.manifold = manifold;
    }

    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    @Override
    public Vector sampleDirection(Vector point, Random rand) {
        return matrix.multiply(manifold.sampleVelocity(point, rand));
    }
}
