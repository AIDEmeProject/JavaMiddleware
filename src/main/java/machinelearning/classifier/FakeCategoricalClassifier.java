package machinelearning.classifier;

import utils.linalg.Vector;

public class FakeCategoricalClassifier extends CategoricalClassifier {
    public FakeCategoricalClassifier(CategoricalClassifier clf) {
        super(clf);
    }

    @Override
    public double probability(Vector vector) {
        return super.probability(vector) - 0.5;
    }

    public Label predict(Vector vector) {
        return super.predict(vector);
    }
}
