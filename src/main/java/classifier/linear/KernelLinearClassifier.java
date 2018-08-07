package classifier.linear;

import classifier.Classifier;
import data.DataPoint;
import data.LabeledPoint;
import utils.linalg.LinearAlgebra;

import java.util.Collection;

public class KernelLinearClassifier implements Classifier {
    private final LinearClassifier linearClassifier;
    private final Collection<LabeledPoint> supportVectors;

    public KernelLinearClassifier(LinearClassifier linearClassifier, Collection<LabeledPoint> supportVectors) {
        this.linearClassifier = linearClassifier;
        this.supportVectors = supportVectors;

    }

    private double kernel(double[] x, double[] y){
        return Math.exp(-LinearAlgebra.sqDistance(x, y) / x.length);
    }

    private DataPoint applyKernel(DataPoint point){
        double[] kernalizedPoint = new double[supportVectors.size()];

        int i = 0;
        for (DataPoint pt : supportVectors) {
            kernalizedPoint[i++] = kernel(pt.getData(), point.getData());
        }

        return new DataPoint(point.getRow(), point.getId(), kernalizedPoint);
    }

    @Override
    public double probability(DataPoint point) {
        return linearClassifier.probability(applyKernel(point));
    }

    @Override
    public int predict(DataPoint point) {
        return linearClassifier.predict(applyKernel(point));
    }
}
