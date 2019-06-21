package machinelearning.active.learning.versionspace.manifold;

import utils.Validator;
import utils.linalg.Vector;

public abstract class Geodesic {
    protected final Vector center;
    protected final Vector velocity;

    public Geodesic(Vector center, Vector velocity) {
        validateCenterAndVelocity(center, velocity);
        this.center = center;
        this.velocity = velocity;
    }

    protected void validateCenterAndVelocity(Vector center, Vector velocity) {
        Validator.assertEquals(center.dim(), velocity.dim());
        Validator.assertPositive(velocity.squaredNorm());
    }

    public final int dim() {
        return center.dim();
    }

    public final Vector getCenter() {
        return center;
    }

    public final Vector getVelocity() {
        return velocity;
    }

    public abstract Vector getPoint(double proportion);

    public final GeodesicSegment getSegment(double lowerBound, double upperBound) {
        return new GeodesicSegment(this, lowerBound, upperBound);
    }
}
