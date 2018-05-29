package exceptions;

/**
 * To be thrown when attempting to retrieve an unlabeled point when all points in the database have previously been labeled.
 */
public class EmptyUnlabeledSetException extends RuntimeException {
    public EmptyUnlabeledSetException() {
        super("All points have been labeled, cannot perform requested operation over unlabeled set!");
    }
}
