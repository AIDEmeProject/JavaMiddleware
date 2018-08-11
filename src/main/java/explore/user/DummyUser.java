package explore.user;

import data.DataPoint;

import java.util.Set;

/**
 * The DummyUser is a special kind of annotator. It knows the which data points are positive in advance.
 * It is useful when developing new algorithms and making benchmarks over known datasets.
 */
public class DummyUser implements User {
    /**
     * true labels
     */
    private Set<Long> positiveKeys;

    /**
     * @param positiveKeys: set of data point's indexes in the target set
     * @throws IllegalArgumentException if array is empty, contains any number different from 0 or 1, or all labels are identical
     */
    public DummyUser(Set<Long> positiveKeys) {
        if (positiveKeys.isEmpty()){
            throw new IllegalArgumentException("Positive key set cannot be empty.");
        }
        this.positiveKeys = positiveKeys;
    }

    /**
     * @return 1 if positiveKeys contains data point's id; else 0
     */
    @Override
    public int getLabel(DataPoint point) {
        return positiveKeys.contains(point.getId()) ? 1 : 0;
    }
}
