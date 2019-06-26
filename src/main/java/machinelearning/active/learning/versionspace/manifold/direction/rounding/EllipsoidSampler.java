package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;

public class EllipsoidSampler implements DirectionSampler {
    private final Matrix matrix;
    private final Manifold manifold;

    public EllipsoidSampler(Ellipsoid ellipsoid, Manifold manifold) {
        this.matrix = ellipsoid.getCholeskyFactor();
        this.manifold = manifold;
    }

    @Override
    public Vector sampleDirection(Vector point, Random rand) {
        return matrix.multiply(manifold.sampleVelocity(point, rand));
    }
}
