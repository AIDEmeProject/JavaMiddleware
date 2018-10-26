package explore.user;

import data.DataPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactoredUserTest {
    private List<Set<Long>> positiveKeysPerSubspace;
    private FactoredUser factoredUser;

    @Test
    void constructor_emptyListOfPositiveKeys_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new FactoredUser(new ArrayList<>()));
    }

    @Test
    void constructor_listContainsEmptyPositiveKeysSet_throwsException() {
        positiveKeysPerSubspace = new ArrayList<>(new HashSet<>());
        assertThrows(IllegalArgumentException.class, () -> new FactoredUser(positiveKeysPerSubspace));
    }

    @Test
    void getLabel_dataPointBelongsToNoIdSet_returnsExpectedLabelGroup() {
        Label[] expected = new Label[]{Label.NEGATIVE, Label.NEGATIVE};
        assertLabelingIsCorrect(0, expected, new Long[]{2L}, new Long[]{1L});
    }

    @Test
    void getLabel_dataPointBelongsToSingleIdSet_returnsExpectedLabelGroup() {
        Label[] expected = new Label[]{Label.POSITIVE, Label.NEGATIVE};
        assertLabelingIsCorrect(0, expected, new Long[]{0L}, new Long[]{1L});
    }

    @Test
    void getLabel_dataPointBelongsToBothIdSet_returnsExpectedLabelGroup() {
        Label[] expected = new Label[]{Label.POSITIVE, Label.POSITIVE};
        assertLabelingIsCorrect(0, expected, new Long[]{0L}, new Long[]{0L, 1L});
    }

    private void assertLabelingIsCorrect(long id, Label[] expected, Long[]... lists) {
        DataPoint dataPoint = new DataPoint(id, Vector.FACTORY.zeros(1));
        setFactoredUserFromArrays(lists);
        assertEquals(new LabelGroup(expected), factoredUser.getLabel(dataPoint));
    }

    private void setFactoredUserFromArrays(Long[]... lists) {
        positiveKeysPerSubspace = new ArrayList<>();
        for (Long[] ids : lists) {
            positiveKeysPerSubspace.add(new HashSet<>(Arrays.asList(ids)));
        }
        factoredUser = new FactoredUser(positiveKeysPerSubspace);

    }

}