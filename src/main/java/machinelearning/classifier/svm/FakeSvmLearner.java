package machinelearning.classifier.svm;

import data.LabeledDataset;
import machinelearning.classifier.margin.FakeKernelClassifier;
import machinelearning.classifier.margin.KernelClassifier;
import utils.linalg.Vector;

public class FakeSvmLearner extends SvmLearner {
    public FakeSvmLearner(SvmLearner learner) {
        super(learner);
    }

    @Override
    public KernelClassifier fit(LabeledDataset labeledPoints, Vector sampleWeights) {
        return new FakeKernelClassifier(super.fit(labeledPoints, sampleWeights));
    }
}
