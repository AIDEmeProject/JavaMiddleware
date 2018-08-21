package machinelearning.active.learning.versionspace;

import data.LabeledPoint;
import machinelearning.active.learning.versionspace.convexbody.HitAndRunSampler;
import machinelearning.classifier.margin.KernelClassifier;
import machinelearning.classifier.margin.LinearClassifier;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class defines the Version Space for the {@link KernelClassifier} classifier. It is defined by the set of
 * equations:
 *
 *  \( y_i  \left(b + \sum_i \alpha_i^t k(x_i, x_j) \right) &gt; 0 \)
 *
 * Note that its dimension equals the number of support vectors (which increases as an Active Learning algorighm runs).
 * Sampling from this version space can be done in the same way as for the {@link LinearVersionSpace}, the only difference
 * is we need to construct the Kernel Matrix of the labeled data beforehand.
 *
 * @see KernelClassifier
 * @see LinearVersionSpace
 * @see HitAndRunSampler
 */
public class KernelVersionSpace implements VersionSpace {
    /**
     * {@link LinearVersionSpace} instance used for sampling
     */
    private final LinearVersionSpace linearVersionSpace;

    /**
     * {@link Kernel} function
     */
    private final Kernel kernel;

    /**
     * @param sampler: Hit-and-Run sampler used for sampling the Version Space
     * @param addIntercept: whether to sample the bias parameter
     * @param kernel: the kernel function
     * @throws NullPointerException if sampler or kernel is null
     */
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
    public KernelClassifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples) {
        LinearClassifier[] linearClassifiers = linearVersionSpace.sample(computeKernelMatrix(labeledPoints), numSamples);

        KernelClassifier[] kernelClassifiers = new KernelClassifier[numSamples];
        for (int i = 0; i < numSamples; i++) {
            kernelClassifiers[i] = new KernelClassifier(linearClassifiers[i], labeledPoints, kernel);
        }

        return kernelClassifiers;
    }
}