package machinelearning.classifier;

public interface ExtendedClassifier {
    void update(double[] point, Label label);

    ExtendedLabel predict(double[] point);
}