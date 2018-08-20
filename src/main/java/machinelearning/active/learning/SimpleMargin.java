package machinelearning.active.learning;

import machinelearning.active.ActiveLearner;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.svm.SvmClassifier;
import machinelearning.classifier.svm.SvmLearner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import utils.OptimumFinder;
import utils.Validator;

import java.util.Collection;

/**
 * Simple Margin is a Active Learning technique introduced in [1]. It approximates a version space cutting technique
 * by relying on properties of the SVM classifier. However, the algorithm can be stated in very simple terms: retrieve
 * at every iteration the point closest to the SVM's current decision boundary.
 *
 * References:
 *  [1] Support Vector Machine Active Learning with Applications to Text Classification
 *      Simon Tong, Daphne Koller
 *      Journal of Machine Learning Research, 2001
 *
 * @see <a href="http://www.jmlr.org/papers/volume2/tong01a/tong01a.pdf">Original paper</a>
 */
public class SimpleMargin extends ActiveLearner {
    /**
     * SVM classifier
     */
    private SvmClassifier classifier;

    public SimpleMargin(SvmLearner learner) {
        super(learner);
    }

    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        classifier = (SvmClassifier) super.fit(labeledPoints);
        return classifier;
    }

    /**
     * Retrieve point closest to the decision boundary
     * @param data: labeled data object
     * @return row index of unlabeled point closest to decision boundary
     * @throws IllegalArgumentException if unlabeled set is empty
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        Validator.assertNotEmpty(data.getUnlabeledPoints());
        return OptimumFinder.minimizer(data.getUnlabeledPoints(), pt -> Math.abs(classifier.margin(pt))).getOptimizer();
    }
}
