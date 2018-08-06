package classifier.linear;

import classifier.Classifier;
import data.DataPoint;
import utils.linalg.LinearAlgebra;


public class LinearClassifier implements Classifier {
    private double bias;
    private double[] weights;

    public LinearClassifier(double bias, double[] weights) {
        this.bias = bias;
        this.weights = weights;
    }

    public LinearClassifier(double[] weights, boolean hasBias) {
        if (hasBias){
            this.bias = weights[0];
            this.weights = new double[weights.length-1];
            System.arraycopy(weights, 1, this.weights, 0, this.weights.length);
        }
        else {
            this.bias = 0;
            this.weights = weights;
        }
    }

    public double getBias() {
        return bias;
    }

    public double[] getWeights() {
        //TODO: return copy so to avoid unintended changes?
        return weights;
    }

    private double margin(double[] x){
        return bias + LinearAlgebra.dot(x, weights);
    }

    @Override
    public double probability(DataPoint point) {
        return 1. / (1. + Math.exp(-margin(point.getData())));
    }

    @Override
    public int predict(DataPoint point) {
        return margin(point.getData()) > 0 ? 1 : 0;
    }
}
