package utils.versionspace;

import classifier.linear.KernelLinearClassifier;
import classifier.linear.LinearClassifier;
import data.DataPoint;
import data.LabeledPoint;
import sampling.HitAndRunSampler;
import utils.Validator;
import utils.linalg.LinearAlgebra;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A Kernel classifier is defined as:
 *
 *  \( h(x) = sign \left(b + \sum_i \alpha_i k(x_i, x) \right) \)
 *
 * As we can see, it is a generalization of a Linear Classifier, but each data points is substituted by it Kernel application:
 *
 *      \( K = [k(x_i, x)] \)
 *
 * For this set of classifiers, we can define the Version Space at iteration \( t \) by the set of equations:
 *
 *  \( y_i  \left(b + \sum_i \alpha_i^t k(x_i, x_j) \right) &gt; 0
 *
 * Note that its dimension increases with each iteration. Sampling from this version space can be done in the same way
 * as in the {@link LinearVersionSpace} case, we only need to construct the Kernel Matrix of the labeled data beforehand.
 *
 * @see LinearClassifier
 * @see LinearVersionSpace
 * @see HitAndRunSampler
 * @see utils.convexbody.PolyhedralCone
 */
public class KernelVersionSpace implements VersionSpace {
    /**
     * {@link LinearVersionSpace} instance used for sampling
     */
    private final LinearVersionSpace linearVersionSpace;

    public KernelVersionSpace(HitAndRunSampler sampler, boolean addIntercept) {
        Validator.assertNotNull(sampler);
        this.linearVersionSpace = new LinearVersionSpace(sampler, addIntercept);
    }

    /**
     * Computes the RBF kernel function k(x,y)
     */
    private double kernel(double[] x, double[] y){
        return Math.exp(-LinearAlgebra.sqDistance(x, y) / x.length);
    }

    /**
     * Given X and a collection {x_1, ..., x_n}, computes {k(x_1, X), ..., k(x_n, X)}
     */
    private LabeledPoint applyKernel(LabeledPoint point, Collection<? extends DataPoint> pointCollection){
        double[] kernalizedPoint = new double[pointCollection.size()];

        int i = 0;
        for (DataPoint pt : pointCollection) {
            kernalizedPoint[i++] = kernel(pt.getData(), point.getData());
        }

        return new LabeledPoint(point.getRow(), point.getId(), kernalizedPoint, point.getLabel());
    }

    /**
     * Given {x_1, ..., x_n}, computes the kernel matrix K = [k(x_i, x_j)]
     */
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
