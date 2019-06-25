package machinelearning.classifier.margin;

import utils.linalg.Matrix;
import utils.linalg.Vector;

public class HyperPlane {
    /**
     * Bias parameter
     */
    private double bias;

    /**
     * Weight vector
     */
    protected Vector weights;

    public HyperPlane(double bias, Vector weights) {
        this.bias = bias;
        this.weights = weights;
    }

    public Vector getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    public int dim() {
        return weights.dim();
    }

    public double margin(Vector point) {
        return weights.dot(point) + bias;
    }

    public Vector margin(Matrix points) {
        return points.multiply(weights).iScalarAdd(bias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HyperPlane)) return false;
        HyperPlane that = (HyperPlane) o;
        return Double.compare(that.bias, bias) == 0 && weights.equals(that.weights);
    }
}
