package machinelearning.classifier.margin;

import data.DataPoint;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Collection;

/**
 * A Kernel classifier is defined as:
 *
 *   \( h(x) = sign \left(b + \sum_i \alpha_i k(x_i, x) \right) \)
 *
 * where \(b\) is the bias, \(\alpha_i\) are the weights, and \(\{x_i\}\) are the support vectors.
 *
 * As we can see, it is a generalization of a {@link LinearClassifier}, but each data points is substituted by it
 * Kernel application:
 *
 *   \( K = [k(x_i, x)] \)
 *
 * Similarly to Linear Classifiers, probability predictions are obtained through an application of the Sigmoid function.
 *
 * For the time being, we only support the RBF kernel.
 */

public class KernelClassifier extends MarginClassifier {
    /**
     * linear classifier used for computing probabilities and making predictions
     */
    private final LinearClassifier linearClassifier;

    /**
     * weight vectors used for computing the kernel matrix
     */
    private final Collection<? extends DataPoint> supportVectors;

    private final Kernel kernel;

    /**
     * @param bias: the bias \(b\)
     * @param weights: the weights \(\alpha_i\)
     * @param supportVectors: collection of support vectors
     * @throws NullPointerException if kernel is null
     * @throws IllegalArgumentException if the number of weights and support vectors are different
     */
    public KernelClassifier(double bias, double[] weights, Collection<? extends DataPoint> supportVectors, Kernel kernel) {
        this(new LinearClassifier(bias, weights), supportVectors, kernel);
    }

    /**
     * @param linearClassifier: a {@link LinearClassifier} instance containing the bias the weight parameters
     * @param supportVectors: collection of support vectors
     * @throws NullPointerException if linearClassifier or kernel is null
     * @throws IllegalArgumentException if linearClassifier dimension is different from the number of support vectors
     */
    public KernelClassifier(LinearClassifier linearClassifier, Collection<? extends DataPoint> supportVectors, Kernel kernel) {
        Validator.assertNotNull(linearClassifier);
        Validator.assertNotNull(kernel);
        Validator.assertEquals(linearClassifier.getDim(), supportVectors.size());

        this.linearClassifier = linearClassifier;
        this.supportVectors = supportVectors;
        this.kernel = kernel;
    }

    @Override
    public double margin(Vector x) {
        double[] kernelVector = kernel.compute(supportVectors, x);
        return linearClassifier.margin(Vector.FACTORY.make(kernelVector));
    }
}
