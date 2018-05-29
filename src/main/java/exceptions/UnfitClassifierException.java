package exceptions;

public class UnfitClassifierException extends RuntimeException {
    public UnfitClassifierException() {
        super("Classifier must be trained over labeled data with fit() before calling this method.");
    }
}
