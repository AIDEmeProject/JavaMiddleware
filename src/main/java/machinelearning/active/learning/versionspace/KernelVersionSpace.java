package machinelearning.active.learning.versionspace;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.classifier.KernelMajorityVote;
import machinelearning.classifier.MajorityVote;
import machinelearning.classifier.margin.KernelClassifier;
import machinelearning.classifier.margin.LinearClassifier;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Matrix;

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
public class KernelVersionSpace implements VersionSpace<LinearClassifier> {
    /**
     * {@link LinearVersionSpace} instance used for sampling
     */
    private final LinearVersionSpace linearVersionSpace;

    /**
     * {@link Kernel} function
     */
    private final Kernel kernel;

    /**
     * @param linearVersionSpace: linear version space instance
     * @param kernel: the kernel function
     * @throws NullPointerException if sampler or kernel is null
     */
    public KernelVersionSpace(LinearVersionSpace linearVersionSpace, Kernel kernel) {
        Validator.assertNotNull(linearVersionSpace);
        Validator.assertNotNull(kernel);
        this.linearVersionSpace = linearVersionSpace;
        this.kernel = kernel;
    }

    @Override
    public KernelMajorityVote sample(LabeledDataset labeledPoints, int numSamples) {
        Matrix kernelMatrix = kernel.compute(labeledPoints.getData());
        LabeledDataset kernelLabeledPoints =  labeledPoints.copyWithSameIndexesAndLabels(kernelMatrix);
        MajorityVote<LinearClassifier> linearClassifiers = linearVersionSpace.sample(kernelLabeledPoints, numSamples);
        return new KernelMajorityVote(linearClassifiers.getClassifiers(), labeledPoints.getData(), kernel);
    }
}
