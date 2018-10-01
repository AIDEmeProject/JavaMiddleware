package explore.user;

import data.DataPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;
import utils.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User for the conjunctive query scenario. In this case, the user must provide a collection of partial labels, which will
 * be pieced together in a final label through a CONJUNCTION of the partial labels.
 */
public class FactoredUser implements User {
    private final List<UserStub> partialUsers;

    /**
     * @param listOfPositiveKeys: list of all positive keys in each subspace
     * @throws IllegalArgumentException if input is empty
     */
    public FactoredUser(List<Set<Long>> listOfPositiveKeys) {
        Validator.assertNotEmpty(listOfPositiveKeys);
        this.partialUsers = listOfPositiveKeys.stream()
                .map(UserStub::new)
                .collect(Collectors.toList());
    }

    @Override
    public LabelGroup getLabel(DataPoint point) {
        return new LabelGroup(
                partialUsers.stream()
                        .map(x -> x.getLabel(point))
                        .toArray(Label[]::new)
        );
    }
}
