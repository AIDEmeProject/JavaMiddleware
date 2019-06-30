package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.classifier.margin.HyperPlane;
import utils.linalg.CholeskyDecomposition;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public interface Ellipsoid {
    int dim();

    Vector getCenter();

    Matrix getScale();

    Matrix getL();

    Vector getD();

    default Matrix getCholeskyFactor() {
        return new CholeskyDecomposition(getScale()).getL();
    }

    boolean cut(HyperPlane hyperplane);
}
