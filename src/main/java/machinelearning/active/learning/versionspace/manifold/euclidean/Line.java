package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.Geodesic;
import utils.linalg.Vector;

public class Line extends Geodesic {

    public Line(Vector center, Vector velocity) {
        super(center, velocity);
    }

    @Override
    public final Vector getPoint(double t) {
        return center.add(velocity.scalarMultiply(t));
    }
}
