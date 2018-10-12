package machinelearning.classifier;

import machinelearning.classifier.margin.LinearClassifier;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class KernelMajorityVote extends MajorityVote<LinearClassifier> {
    private Kernel kernel;
    private Matrix supportVectors;

    public KernelMajorityVote(LinearClassifier[] classifiers, Matrix supportVectors, Kernel kernel) {
        super(classifiers);
        Validator.assertEquals(classifiers[0].getDim(), supportVectors.rows());
        this.kernel = kernel;
        this.supportVectors = supportVectors;
    }

    @Override
    public double probability(Vector vector) {
        return super.probability(kernel.compute(supportVectors, vector));
    }
}
