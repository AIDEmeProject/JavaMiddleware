package utils.versionspace;

import classifier.Classifier;
import classifier.linear.KernelLinearClassifier;
import classifier.linear.LinearClassifier;
import data.DataPoint;
import data.LabeledPoint;
import sampling.HitAndRunSampler;
import utils.Validator;
import utils.linalg.LinearAlgebra;

import java.util.ArrayList;
import java.util.Collection;

public class KernelVersionSpace implements VersionSpace {
    private final LinearVersionSpace linearVersionSpace;

    public KernelVersionSpace(HitAndRunSampler sampler, boolean addIntercept) {
        Validator.assertNotNull(sampler);
        this.linearVersionSpace = new LinearVersionSpace(sampler, addIntercept);
    }

    private double kernel(double[] x, double[] y){
        return Math.exp(-LinearAlgebra.sqDistance(x, y) / x.length);
    }

    private LabeledPoint applyKernel(LabeledPoint point, Collection<? extends DataPoint> pointCollection){
        double[] kernalizedPoint = new double[pointCollection.size()];

        int i = 0;
        for (DataPoint pt : pointCollection) {
            kernalizedPoint[i++] = kernel(pt.getData(), point.getData());
        }

        return new LabeledPoint(point.getRow(), point.getId(), kernalizedPoint, point.getLabel());
    }

    private Collection<LabeledPoint> applyKernel(Collection<LabeledPoint> pointCollection){
        Collection<LabeledPoint> kernalizedPoints = new ArrayList<>(pointCollection.size());

        for (LabeledPoint pt : pointCollection) {
           kernalizedPoints.add(applyKernel(pt, pointCollection));
        }

        return kernalizedPoints;
    }

    @Override
    public KernelLinearClassifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples) {
        // apply kernel function
        Collection<LabeledPoint> kernalizedPoints = applyKernel(labeledPoints);
        LinearClassifier[] linearClassifiers = linearVersionSpace.sample(kernalizedPoints, numSamples);

        KernelLinearClassifier[] kernelLinearClassifiers = new KernelLinearClassifier[numSamples];
        for (int i = 0; i < numSamples; i++) {
            kernelLinearClassifiers[i] = new KernelLinearClassifier(linearClassifiers[i], labeledPoints);
        }

        return kernelLinearClassifiers;
    }
}
