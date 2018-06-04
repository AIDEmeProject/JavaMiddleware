package learner;

import classifier.SVM.SvmClassifier;
import data.LabeledData;

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
public class SimpleMargin implements Learner {
    private SvmClassifier svm;

    public SimpleMargin(SvmClassifier svm) {
        this.svm = svm;
    }

    @Override
    public void fit(LabeledData data) {
        svm.fit(data);
    }

    @Override
    public double probability(LabeledData data, int row) {
        return svm.probability(data, row);
    }

    @Override
    public int predict(LabeledData data, int row) {
        return svm.predict(data, row);
    }

    /**
     * Retrieve point closest to the decision boundary
     * @param data: labeled data object
     * @return row index of unlabeled point closest to decision boundary
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        return data.retrieveMinimizerOverUnlabeledData((dt,row) -> Math.abs(svm.margin(dt, row)));
    }
}
