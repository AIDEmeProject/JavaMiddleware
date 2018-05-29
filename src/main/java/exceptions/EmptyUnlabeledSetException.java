package exceptions;

public class EmptyUnlabeledSetException extends RuntimeException {
    public EmptyUnlabeledSetException() {
        super("All points have been labeled, cannot perform requested operation over unlabeled set!");
    }
}
