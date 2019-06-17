package machinelearning.active.learning.versionspace.manifold.sphere;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import utils.Validator;
import utils.linalg.Vector;

public class GreatCircle extends Geodesic {
    public GreatCircle(Vector center, Vector velocity) {
        super(center, velocity);
        this.center.iNormalize(1.0);
        this.velocity.iNormalize(1.0);
    }

    @Override
    protected final void validateCenterAndVelocity(Vector center, Vector velocity) {
        Validator.assertEquals(center.dim(), velocity.dim());
        Validator.assertEquals(center.dot(velocity), 0.0);
    }

    @Override
    public final Vector getPoint(double t) {
        Vector point = center.scalarMultiply(Math.cos(t));
        point.iAdd(velocity.scalarMultiply(Math.sin(t)));
        return point;
    }
}
