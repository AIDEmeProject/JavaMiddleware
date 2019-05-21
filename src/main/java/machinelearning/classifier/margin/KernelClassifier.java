package machinelearning.classifier.margin;

import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

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
    private final Matrix supportVectors;

    private final Kernel kernel;

    /**
     * @param bias: the bias \(b\)
     * @param weights: the weights \(\alpha_i\)
     * @param supportVectors: collection of support vectors
     * @throws NullPointerException if kernel is null
     * @throws IllegalArgumentException if the number of weights and support vectors are different
     */
    public KernelClassifier(double bias, Vector weights, Matrix supportVectors, Kernel kernel) {
        this(new LinearClassifier(bias, weights), supportVectors, kernel);
    }

    /**
     * @param linearClassifier: a {@link LinearClassifier} instance containing the bias the weight parameters
     * @param supportVectors: collection of support vectors
     * @throws NullPointerException if linearClassifier or kernel is null
     * @throws IllegalArgumentException if linearClassifier dimension is different from the number of support vectors
     */
    public KernelClassifier(LinearClassifier linearClassifier, Matrix supportVectors, Kernel kernel) {
        Validator.assertNotNull(linearClassifier);
        Validator.assertNotNull(kernel);
        Validator.assertEquals(linearClassifier.getDim(), supportVectors.rows());

        this.linearClassifier = linearClassifier;
        this.supportVectors = supportVectors;
        this.kernel = kernel;
    }

    public KernelClassifier(KernelClassifier clf) {
        this.linearClassifier = clf.linearClassifier;
        this.supportVectors = clf.supportVectors;
        this.kernel = clf.kernel;
    }

    @Override
    public double margin(Vector x) {
        return linearClassifier.margin(kernel.compute(supportVectors, x));
    }

    @Override
    public Vector margin(Matrix xs) {
        return linearClassifier.margin(kernel.compute(xs, supportVectors));
    }
}
