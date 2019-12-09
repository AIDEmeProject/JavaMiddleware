package machinelearning.active.learning.versionspace;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.KernelMajorityVote;
import machinelearning.classifier.margin.KernelClassifier;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Matrix;

/**
 * This class defines the Version Space for the {@link KernelClassifier} classifier. It is defined by the set of
 * equations:
 *
 *  \( y_i  \left(b + \sum_i \alpha_i^t k(x_i, x_j) \right) &gt; 0 \)
 *
 * Note that its dimension equals the number of support vectors (which increases as an Active Learning algorithm runs).
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
    private final VersionSpace versionSpace;

    /**
     * {@link Kernel} function
     */
    private final Kernel kernel;

    /**
     * @param versionSpace: linear version space instance
     * @param kernel: the kernel function
     * @throws NullPointerException if sampler or kernel is null
     */
    public KernelVersionSpace(VersionSpace versionSpace, Kernel kernel) {
        Validator.assertNotNull(versionSpace);
        Validator.assertNotNull(kernel);
        this.versionSpace = versionSpace;
        this.kernel = kernel;
    }

    @Override
    public KernelMajorityVote sample(LabeledDataset labeledPoints, int numSamples) {
        Matrix kernelMatrix = kernel.compute(labeledPoints.getData());
        LabeledDataset kernelLabeledPoints = labeledPoints.copyWithSameIndexesAndLabels(kernelMatrix);
        Classifier linearMajorityVote = versionSpace.sample(kernelLabeledPoints, numSamples);
        return new KernelMajorityVote(linearMajorityVote, labeledPoints.getData(), kernel);
    }
}
