package learner;

import classifier.SVM.SvmClassifier;
import data.LabeledData;

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

    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        return data.retrieveMinimizerOverUnlabeledData((dt,row) -> Math.abs(svm.margin(dt, row)));
    }
}
