package exceptions;

/**
 * To be thrown when attempting to train a classifier over an empty collection of labeled points.
 */
public class EmptyLabeledSetException extends RuntimeException {
    public EmptyLabeledSetException() {
        super("Cannot fit model over empty labeled set.");
    }
}
