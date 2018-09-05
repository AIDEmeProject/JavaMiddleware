package machinelearning.active.learning;

import data.LabeledPoint;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.MarginRanker;
import machinelearning.classifier.svm.SvmLearner;

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
public class SimpleMargin implements ActiveLearner {
    /**
     * SVM classifier
     */
    private SvmLearner svmLearner;

    public SimpleMargin(SvmLearner svmLearner) {
        this.svmLearner = svmLearner;
    }

    @Override
    public Ranker fit(Collection<LabeledPoint> labeledPoints) {
        return new MarginRanker(svmLearner.fit(labeledPoints));
    }
}
