package machinelearning.classifier;

/**
 * Update and prediction of three-class classifiers
 */
public interface ExtendedClassifier {
    /**
     * Update TSM structure at each iteration
     * @param point latest data points
     * @param label three-class
     */
    void update(double[] point, Label label);

    /**
     * Prediction of three-class classifier
     * @param point
     * @return {-1, 0, 1}
     */
    ExtendedLabel predict(double[] point);
}