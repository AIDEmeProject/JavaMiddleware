package utils.versionspace;

import classifier.kernel.KernelLinearClassifier;
import classifier.linear.LinearClassifier;
import classifier.kernel.Kernel;
import data.LabeledPoint;
import sampling.HitAndRunSampler;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class defines the Version Space for the {@link KernelLinearClassifier} classifier. It is defined by the set of
 * equations:
 *
 *  \( y_i  \left(b + \sum_i \alpha_i^t k(x_i, x_j) \right) &gt; 0 \)
 *
 * Note that its dimension equals the number of support vectors (which increases as an Active Learning algorighm runs).
 * Sampling from this version space can be done in the same way as for the {@link LinearVersionSpace}, the only difference
 * is we need to construct the Kernel Matrix of the labeled data beforehand.
 *
 * @see KernelLinearClassifier
 * @see LinearVersionSpace
 * @see HitAndRunSampler
 */
public class KernelVersionSpace implements VersionSpace {
    /**
     * {@link LinearVersionSpace} instance used for sampling
     */
    private final LinearVersionSpace linearVersionSpace;

    private final Kernel kernel;

    public KernelVersionSpace(HitAndRunSampler sampler, boolean addIntercept, Kernel kernel) {
        Validator.assertNotNull(sampler);
        Validator.assertNotNull(kernel);
        this.linearVersionSpace = new LinearVersionSpace(sampler, addIntercept);
        this.kernel = kernel;
    }

    private Collection<LabeledPoint> computeKernelMatrix(Collection<LabeledPoint> labeledPoints){
        // apply kernel function
        double[][] kernelMatrix = kernel.compute(labeledPoints);

        int i = 0;
        Collection<LabeledPoint> kernelLabeledPoints = new ArrayList<>(labeledPoints.size());
        for (LabeledPoint point : labeledPoints) {
            kernelLabeledPoints.add(point.clone(kernelMatrix[i++]));
        }

        return kernelLabeledPoints;
    }

    @Override
    public KernelLinearClassifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples) {
        LinearClassifier[] linearClassifiers = linearVersionSpace.sample(computeKernelMatrix(labeledPoints), numSamples);

        KernelLinearClassifier[] kernelLinearClassifiers = new KernelLinearClassifier[numSamples];
        for (int i = 0; i < numSamples; i++) {
            kernelLinearClassifiers[i] = new KernelLinearClassifier(linearClassifiers[i], labeledPoints, kernel);
        }

        return kernelLinearClassifiers;
    }
}
