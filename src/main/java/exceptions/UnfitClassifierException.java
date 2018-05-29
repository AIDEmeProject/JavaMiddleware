package exceptions;

/**
 * To be thrown if attempting to predict class probabilities before training a model over the labeled data.
 */
public class UnfitClassifierException extends RuntimeException {
    public UnfitClassifierException() {
        super("Classifier must be trained over labeled data with fit() before calling this method.");
    }
}
