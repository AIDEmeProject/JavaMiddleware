package machinelearning.classifier.margin;

import utils.linalg.Matrix;
import utils.linalg.Vector;

public class CenteredHyperPlane extends HyperPlane {

    public CenteredHyperPlane(Vector weights) {
        super(0, weights);
    }

    @Override
    public double margin(Vector point) {
        return weights.dot(point);
    }

    @Override
    public Vector margin(Matrix points) {
        return points.multiply(weights);
    }
}
