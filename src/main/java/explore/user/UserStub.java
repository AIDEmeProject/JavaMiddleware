package explore.user;

import data.DataPoint;
import machinelearning.classifier.Label;
import utils.Validator;

import java.util.Set;

/**
 * The UserStub is a special kind of annotator, it knows the which data points are positive in advance.
 * It is useful when developing new algorithms and making benchmarks over known datasets.
 */
public class UserStub implements User {
    /**
     * id's of the positive {@link DataPoint}
     */
    private Set<Long> positiveKeys;

    /**
     * @param positiveKeys: set of data point's indexes in the target set
     * @throws IllegalArgumentException if positiveKeys is empty
     */
    public UserStub(Set<Long> positiveKeys) {
        Validator.assertNotEmpty(positiveKeys);
        this.positiveKeys = positiveKeys;
    }

    /**
     * @return POSITIVE if positiveKeys contains the data point's id; else NEGATIVE
     */
    @Override
    public Label getLabel(DataPoint point) {
        return positiveKeys.contains(point.getId()) ? Label.POSITIVE : Label.NEGATIVE;
    }
}
