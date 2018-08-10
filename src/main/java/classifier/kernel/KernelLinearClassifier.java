package classifier.kernel;

import classifier.Classifier;
import classifier.linear.LinearClassifier;
import data.DataPoint;
import utils.Validator;

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

public class KernelLinearClassifier implements Classifier {
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
     *
     * @param linearClassifier: a {@link LinearClassifier} instance containing the bias the weight parameters
     * @param supportVectors: collection of support vectors
     * @throws NullPointerException if linearClassifier is null
     * @throws IllegalArgumentException if supportVectors are empty
     */
    public KernelLinearClassifier(LinearClassifier linearClassifier, Collection<? extends DataPoint> supportVectors, Kernel kernel) {
        Validator.assertNotNull(linearClassifier);
        Validator.assertNotEmpty(supportVectors);
        Validator.assertNotNull(kernel);

        this.linearClassifier = linearClassifier;
        this.supportVectors = supportVectors;
        this.kernel = kernel;

    }

    @Override
    public double probability(DataPoint point) {
        double[] kernelVector = kernel.compute(supportVectors, point);
        return linearClassifier.probability(point.clone(kernelVector));
    }

    @Override
    public int predict(DataPoint point) {
        double[] kernelVector = kernel.compute(supportVectors, point);
        return linearClassifier.predict(point.clone(kernelVector));
    }
}
