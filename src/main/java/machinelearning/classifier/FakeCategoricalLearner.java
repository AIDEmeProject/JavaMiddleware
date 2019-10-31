package machinelearning.classifier;

import data.LabeledDataset;

public class FakeCategoricalLearner extends CategoricalLearner {
    @Override
    public CategoricalClassifier fit(LabeledDataset labeledPoints) {
        return new FakeCategoricalClassifier(super.fit(labeledPoints));
    }
}
