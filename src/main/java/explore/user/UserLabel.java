package explore.user;

public interface UserLabel {
    /**
     * @return whether the label is POSITIVE
     */
    boolean isPositive();

    /**
     * @return whether the label is NEGATIVE
     */
    boolean isNegative();

    /**
     * @return 1 if POSITIVE, 0 if NEGATIVE
     */
    default int asBinary(){
        return isPositive() ? 1 : 0;
    }

    /**
     * @return 1 if POSITIVE, -1 if NEGATIVE
     */
    default int asSign(){
        return isPositive() ? 1 : -1;
    }

}
