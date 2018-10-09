package explore.user;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserStubTest {
    private IndexedDataset points;
    private UserStub user;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        for (int i = 0; i < 4; i++) {
            builder.add(i, new double[]{i+1});
        }
        points = builder.build();

        Set<Long> positiveKeys = new HashSet<>(Arrays.asList(1L, 2L));
        user = new UserStub(positiveKeys);
    }

    @Test
    void constructor_emptyKeySet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new UserStub(new HashSet<>()));
    }

    @Test
    void getLabel_testAllIndexes_returnsCorrectLabels() {
        Label[] labels = new Label[] {Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE};
        int i = 0;
        for (DataPoint point : points) {
            assertEquals(labels[i++], user.getLabel(point));
        }
    }

    @Test
    void getAllLabels_callGetAllLabels_returnsLabelsArray() {
        assertArrayEquals(new Label[] {Label.NEGATIVE,Label.POSITIVE,Label.POSITIVE,Label.NEGATIVE}, user.getLabel(points));
    }

}